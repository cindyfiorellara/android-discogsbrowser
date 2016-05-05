package work.beltran.discogsbrowser.api.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import work.beltran.discogsbrowser.api.ApiFrontend;
import work.beltran.discogsbrowser.api.network.DiscogsService;

/**
 * Created by Miquel Beltran on 23.04.16.
 * More on http://beltran.work
 */
@Module(includes = {DiscogsModule.class})
public class UserCollectionModule {
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;

    public UserCollectionModule(Scheduler subscribeOnScheduler, Scheduler observeOnScheduler) {
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    @Provides
    @Singleton
    public ApiFrontend provideUserCollection(DiscogsService service) {
        return new ApiFrontend(service, observeOnScheduler, subscribeOnScheduler);
    }
}
