package marvel.erickmaeda.com.marvelcharacters.presenters.characters;

import java.io.IOException;
import java.util.List;

import marvel.erickmaeda.com.marvelcharacters.entities.Character;
import marvel.erickmaeda.com.marvelcharacters.retrofit.RestAdapterProvider;
import marvel.erickmaeda.com.marvelcharacters.retrofit.api.MarvelApi;
import marvel.erickmaeda.com.marvelcharacters.retrofit.api.MarvelApiUtils;
import marvel.erickmaeda.com.marvelcharacters.retrofit.entities.character_response.ResponseCharacter;
import marvel.erickmaeda.com.marvelcharacters.system.utils.RxUtils;
import marvel.erickmaeda.com.marvelcharacters.ui.activities.CharactersView;
import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CharactersPresenterImpl implements CharactersPresenter {

    private CharactersView view;
    private List<Character> characters;
    private MarvelApi api;
    private Subscription subscription;

    public CharactersPresenterImpl(CharactersView view) {
        this.view = view;
        this.api = new RestAdapterProvider().getApi();
    }

    @Override
    public void create() {
        if (characters == null || characters.size() <= 0)
            loadCharacters();
    }

    @Override
    public void setView(CharactersView view) {
        this.view = view;
        if (characters != null)
            view.setCharacters(characters);
    }

    @Override
    public void loadCharacters(String nameStartsWith) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = RxUtils.makeObservable(() -> {
            Response<ResponseCharacter> response = null;
            try {
                response = api.getCharacterWhereNameStartsWith(nameStartsWith, MarvelApiUtils.mountHash(), String.valueOf(MarvelApiUtils.lastCurrentTimeMounted)).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((response) -> {
                    if (response.isSuccessful()) {
                        view.setCharacters(response.body().getData().getResults());
                        this.characters = response.body().getData().getResults();
                    } else {
                        view.onError(response.code() + " | " + response.message());
                    }
                }, (Throwable::printStackTrace));
    }

    @Override
    public void loadCharacters() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = RxUtils.makeObservable(() -> {
            Response<ResponseCharacter> response = null;
            try {
                response = api.getCharacters(MarvelApiUtils.mountHash(), String.valueOf(MarvelApiUtils.lastCurrentTimeMounted)).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((response) -> {
                    if (response.isSuccessful()) {
                        view.setCharacters(response.body().getData().getResults());
                        this.characters = response.body().getData().getResults();
                    } else {
                        view.onError(response.code() + " | " + response.message());
                    }
                }, (Throwable::printStackTrace));
    }
}