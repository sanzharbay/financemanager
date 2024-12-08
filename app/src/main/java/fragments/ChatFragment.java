package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;

import java.util.ArrayList;
import java.util.Comparator;

import Adapters.ChatAdapter;
import Utils.Globals;
import models.Transfer;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Transfer> transfers = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        transfers = Globals.getInstance().getTransfers();

        transfers.sort(Comparator.comparingLong(Transfer::getTime));
        ChatAdapter chatAdapter = new ChatAdapter(transfers);
        recyclerView.setAdapter(chatAdapter);

        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
