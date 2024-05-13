package com.example.triptracks.Presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.example.triptracks.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> dates;
    private final String category;
    private final Drawable backgroundDrawable;

    public EventDecorator(Context context, Collection<CalendarDay> dates, String category) {
        this.dates = new HashSet<>(dates);
        this.category = category;
        this.backgroundDrawable = getBackgroundDrawable(context, category);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (backgroundDrawable != null) {
            view.setSelectionDrawable(backgroundDrawable);
        }
    }

    private Drawable getBackgroundDrawable(Context context, String category) {
        String exploracion = context.getResources().getString(R.string.exploraci_n);
        String gastronomia = context.getResources().getString(R.string.gastronom_a);
        String entretenimiento = context.getResources().getString(R.string.entretenimiento);


        if (category.equals(exploracion)) {
            return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_expl);

        } else if (category.equals(gastronomia)) {
            return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_gast);
        } else if (category.equals(entretenimiento)) {
            return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_ent);
        } else {
            return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_def);
        }
    }
}


