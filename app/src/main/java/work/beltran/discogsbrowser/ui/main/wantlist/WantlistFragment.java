package work.beltran.discogsbrowser.ui.main.wantlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.adapter.WrapAdapter;

import rx.Observer;
import work.beltran.discogsbrowser.R;
import work.beltran.discogsbrowser.api.model.UserProfile;
import work.beltran.discogsbrowser.ui.App;
import work.beltran.discogsbrowser.ui.main.common.CustomToolbar;

public class WantlistFragment extends work.beltran.discogsbrowser.ui.main.common.RecordsFragment<WantRecordsAdapter> {

    public static WantlistFragment newInstance() {
        WantlistFragment fragment = new WantlistFragment();
        return fragment;
    }

    @Override
    protected void injectDependencies() {
        ((App) getActivity().getApplication()).getApiComponent().inject(this);
        ((App) getActivity().getApplication()).getApiComponent().inject(adapter);
    }

    @Override
    protected void initHeaderFooter(LayoutInflater inflater, RecyclerView recyclerView, WrapAdapter wrapAdapter) {
        final View header = inflater.inflate(R.layout.header_wantlist, recyclerView, false);
        wrapAdapter.addHeader(header);
        collection.getUserProfile().subscribe(new Observer<UserProfile>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(UserProfile userProfile) {
                TextView userWantlist = (TextView) header.findViewById(R.id.textWantlist);
                userWantlist.setText(getResources().getString(R.string.user_wantlist, userProfile.getUsername()));
                TextView inWantList = (TextView) header.findViewById(R.id.textInWantlist);
                inWantList.setText(getResources().getString(R.string.in_wantlist, userProfile.getNum_wantlist()));

            }
        });
        CustomToolbar.setToolbar(this, header);
        View footer = inflater.inflate(R.layout.footer, recyclerView, false);
        wrapAdapter.addFooter(footer);
    }
}
