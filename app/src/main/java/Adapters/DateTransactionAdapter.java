package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragments.TransactionHistoryFragment;

public class DateTransactionAdapter extends FragmentStateAdapter {
    public DateTransactionAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return(TransactionHistoryFragment.newInstance(position));
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
