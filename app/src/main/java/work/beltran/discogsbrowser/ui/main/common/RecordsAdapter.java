package work.beltran.discogsbrowser.ui.main.common;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import work.beltran.discogsbrowser.R;
import work.beltran.discogsbrowser.api.model.record.Record;
import work.beltran.discogsbrowser.api.network.AveragePrice;
import work.beltran.discogsbrowser.api.network.RecordsSubject;
import work.beltran.discogsbrowser.databinding.CardRecordBinding;
import work.beltran.discogsbrowser.ui.errors.ErrorPresenter;
import work.beltran.discogsbrowser.ui.settings.Settings;

/**
 * Created by Miquel Beltran on 23.04.16.
 * More on http://beltran.work
 */
public abstract class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RecordsAdapter.class.getCanonicalName();
    private RecordsSubject subject;
    protected List<Record> recordList = new ArrayList<>();
    private Picasso picasso;
    private Subscription subscription;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private ErrorPresenter errorPresenter;
    private AveragePrice averagePrice;
    private Settings settings;

    public RecordsAdapter(RecordsSubject subject, Picasso picasso) {
        this.picasso = picasso;
        this.subject = subject;
        subscribe();
    }

    @Inject
    public void setErrorPresenter(ErrorPresenter errorPresenter) {
        this.errorPresenter = errorPresenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_ITEM:
            default:
                CardRecordBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_record, parent, false);
                return new RecordViewHolder(binding);
            case VIEW_PROG:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
                return new ProgressBarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecordViewHolder) {
            onBindViewHolder((RecordViewHolder) holder, position);
        }
    }

    protected void onBindViewHolder(final RecordViewHolder holder, int position) {
        holder.getBinding().setRecord(recordList.get(position));
        picasso.load(recordList.get(position).getBasicInformation().getThumb())
                .tag(this)
                .placeholder(R.drawable.music_record)
                .fit()
                .centerCrop()
                .into(holder.getBinding().recordThumb);
        boolean showPrices = settings.getSharedPreferences().getBoolean(getPreferencePrices(), getPreferencePricesDefault());
        if (showPrices) {
            String type = settings.getSharedPreferences().getString(getPreferencePricesType(), "0");
            averagePrice.getAveragePrice(recordList.get(position), "EUR", type)
                    .subscribe(new Observer<Double>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Double aDouble) {
                            NumberFormat format = NumberFormat.getCurrencyInstance();
                            holder.getBinding().textPrice.setText(format.format(aDouble));
                        }
                    });
        }
    }

    protected abstract boolean getPreferencePricesDefault();

    protected abstract String getPreferencePricesType();

    protected abstract String getPreferencePrices();

    @Override
    public int getItemViewType(int position) {
        if (!subject.getSubject().hasCompleted() && position == recordList.size())
            return VIEW_PROG;
        else
            return VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return recordList.size() + (!subject.getSubject().hasCompleted() ? 1 : 0);
    }

    private void subscribe() {
        subscription = subject
                .getSubject()
                .subscribe(new Observer<Record>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        notifyItemRemoved(recordList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError() " + e.getMessage());
                        errorPresenter.onError(e);
                    }

                    @Override
                    public void onNext(Record record) {
                        Log.d(TAG, "onNext(" + record.getInstance_id() + ")");
                        recordList.add(record);
                        notifyItemInserted(recordList.size() - 1);
                    }
                });
    }

    public void activityOnDestroy() {
        subscription.unsubscribe();
    }

    public void loadMore() {
        Log.d(TAG, "Load More Requested");
        subject.loadMoreData();
    }

    @Inject
    public void setAveragePrice(AveragePrice averagePrice) {
        this.averagePrice = averagePrice;
    }

    @Inject
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        private CardRecordBinding binding;

        public RecordViewHolder(CardRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public CardRecordBinding getBinding() {
            return binding;
        }
    }

    public class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        public ProgressBarViewHolder(View view) {
            super(view);
        }
    }
}
