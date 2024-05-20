
package com.example.triptracks.Domain.LogicaNegocio;

import android.os.AsyncTask;
import com.beastwall.localisation.Localisation;
import com.beastwall.localisation.model.Country;
import com.example.triptracks.Presenter.AuthActivityView;
import com.example.triptracks.Presenter.ItneraryActivityView;

import java.lang.ref.WeakReference;
import java.util.List;

public class LoadCountriesTask extends AsyncTask<Void, Void, List<Country>> {
    private WeakReference<AuthActivityView> activityReference;

    public LoadCountriesTask(AuthActivityView context) {
        this.activityReference = new WeakReference<>(context);
    }

    @Override
    protected List<Country> doInBackground(Void... voids) {

        return Localisation.getAllCountriesStatesAndCities();
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        super.onPostExecute(countries);
        AuthActivityView activity = activityReference.get();
        if (activity != null && !activity.isFinishing()) {
            AuthActivityView.onCountriesLoaded(countries);
        }
    }
}