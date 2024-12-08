package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.financemanager.R;

import java.util.ArrayList;

import models.Category;

public class CourseGVAdapter extends ArrayAdapter<Category> {

    public CourseGVAdapter(@NonNull Context context, ArrayList<Category> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, parent, false);
        }

        Category courseModel = getItem(position);
        TextView courseTV = listItemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listItemView.findViewById(R.id.idIVCourse);


        courseTV.setText(courseModel.getName());
        courseIV.setImageResource(courseModel.getImgID());
        return listItemView;
    }
}

