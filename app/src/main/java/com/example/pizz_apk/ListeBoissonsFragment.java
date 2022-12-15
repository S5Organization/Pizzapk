package com.example.pizz_apk;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pizz_apk.R;
import com.example.pizz_apk.adapters.ListeBoissonsAdapter;
import com.example.pizz_apk.adapters.ListeBurgersAdapter;
import com.example.pizz_apk.adapters.ListePizzasAdapter;
import com.example.pizz_apk.adapters.PlatUniqueListener;
import com.example.pizz_apk.databinding.FragmentListeBoissonsBinding;
import com.example.pizz_apk.databinding.FragmentListeBurgersBinding;
import com.example.pizz_apk.databinding.FragmentListePizzasBinding;
import com.example.pizz_apk.models.PlatPropose;
import com.example.pizz_apk.models.RetroFitRequests;
import com.example.pizz_apk.models.RetroFitResponse;
import com.example.pizz_apk.services.Utils;
import com.example.pizz_apk.viewmodels.ListeBoissonsViewModel;
import com.example.pizz_apk.viewmodels.ListeBurgersViewModel;
import com.example.pizz_apk.viewmodels.ListePizzasViewModel;
import com.example.pizz_apk.viewmodels.PlatUniqueViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeBoissonsFragment extends Fragment {

    FragmentListeBoissonsBinding binding;
    ListeBoissonsViewModel boissonsViewModel;
    PlatUniqueViewModel platUniqueViewModel;
    Context context = getContext();
    PlatUniqueListener listener;
    RetroFitRequests requests;

    public ListeBoissonsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListeBoissonsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requests = Utils.getRetrofitCon(requireContext());
        this.HandleGetBoissons(view);
        binding.imageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_listeBoissonsFragment_to_accueilFragment);
            }
        });



    }

    public void HandleGetBoissons(View view){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("restaurantId",1);
        map.put("typeId", 4);

        Call<RetroFitResponse<ArrayList<PlatPropose>>> call =requests.getPlatsByTypeEtRestaurant(map);

        call.enqueue(new Callback<RetroFitResponse<ArrayList<PlatPropose>>>() {
            @Override
            public void onResponse(Call<RetroFitResponse<ArrayList<PlatPropose>>> call, Response<RetroFitResponse<ArrayList<PlatPropose>>> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    ArrayList<PlatPropose> result = response.body().getResult();
                    //trier les plats pour ne garder que les boissons
                    ArrayList<PlatPropose> boissons = new ArrayList<>();
                    for (PlatPropose plat : result) {
                            boissons.add(plat);
                    }

                    boissonsViewModel = new ViewModelProvider(requireActivity()).get(ListeBoissonsViewModel.class);
                    boissonsViewModel.setListBoissonsLiveData(boissons);
                    platUniqueViewModel = new ViewModelProvider(requireActivity()).get(PlatUniqueViewModel.class);
                    ListeBoissonsAdapter adapter = new ListeBoissonsAdapter(boissonsViewModel.getListBoissonsLiveData().getValue(), context, new PlatUniqueListener() {
                        @Override
                        public void onPlatUniqueClicked(PlatPropose platPropose) {
                            platUniqueViewModel.setSelectedPlat(platPropose);
                            Navigation.findNavController(view).navigate(R.id.action_listeBoissonsFragment_to_platUniqueFragment);
                        }

                        @Override
                        public void onPlatUniqueAllergenesClicked(PlatPropose platPropose) {
                            platUniqueViewModel.setSelectedPlat(platPropose);
                            Navigation.findNavController(view).navigate(R.id.action_listeBoissonsFragment_to_platUniqueFragment);
                        }
                    });
                    binding.rvBoissonsListe.setHasFixedSize(true);
                    binding.rvBoissonsListe.setAdapter(adapter);
                    binding.rvBoissonsListe.setLayoutManager(new LinearLayoutManager(context));

                } else {
                    Utils.requestNotSuccessfulToast(requireContext(), response);
                }
            }
            @Override
            public void onFailure(Call<RetroFitResponse<ArrayList<PlatPropose>>> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }
}