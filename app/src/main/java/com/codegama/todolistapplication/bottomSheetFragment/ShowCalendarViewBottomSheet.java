package com.codegama.todolistapplication.bottomSheetFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.codegama.todolistapplication.R;
import com.codegama.todolistapplication.activity.MainActivity;
import com.codegama.todolistapplication.model.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowCalendarViewBottomSheet extends BottomSheetDialogFragment {

    Unbinder unbinder;
    MainActivity activity;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    List<Task> tasks = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_calendar_view, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        calendarView.setHeaderColor(R.color.colorAccent);
        getSavedTasks();
        back.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void getSavedTasks() {
        CollectionReference tasksRef = db.collection("tasks");
        tasksRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Task retrievedTask = document.toObject(Task.class);
                    tasks.add(retrievedTask);
                }
                calendarView.setEvents(getHighlightedDays());
            }
        });
    }

    public List<EventDay> getHighlightedDays() {
        List<EventDay> events = new ArrayList<>();

        for(int i = 0; i < tasks.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            String[] items1 = tasks.get(i).getDate().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            events.add(new EventDay(calendar, R.drawable.dot));
        }
        return events;
    }

}
