package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.TransferHistoryFragment;

public class DateTransferAdapter extends FragmentStateAdapter {
    public DateTransferAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return(TransferHistoryFragment.newInstance(position));
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
