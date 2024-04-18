package com.example.triptracks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
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
        switch (category) {
            case "Exploración":
                return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_expl);
            case "Gastronomía":
                return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_gast);
            case "Entretenimiento":
                return ContextCompat.getDrawable(context, R.drawable.background_calendar_day_ent);
            case "Default":
                return ContextCompat.getDrawable(context,R.drawable.background_calendar_day_def);
        }
        return null;
    }
}
