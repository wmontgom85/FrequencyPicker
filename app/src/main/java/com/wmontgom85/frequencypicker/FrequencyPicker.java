package com.wmontgom85.frequencypicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class FrequencyPicker extends RelativeLayout {
    private View rootView;
    private Context mContext;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    TextView startDateTextView, endDateTextView, nextDateTextView;

    ArrayList<String> recurringFrequencies;

    boolean frequencyDatesVisible = false, nextBillDateVisible = false;
    Button selectedFrequency, originallySelectedFrequency;

    Button oneTime, weekly, biWeekly, monthly, quarterly, semiAnnually, annually;

    int startDay = -1, startMonth = -1, startYear = -1,
        endDay = -1, endMonth = -1, endYear = -1,
        nextDay = -1, nextMonth = -1, nextYear = -1;

    int frequencyDateHeightEnd, nextBillDateHeightEnd;

    LinearLayout frequencyDates, nextBillDate;

    private boolean animating = false;
    private boolean allowStartDate = true, allowEndDate = true, allowNextBill = false;

    HashMap<String, String> frequencyInformation = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

    public HashMap<String, String> getChosenFrequency() {
        String chosenFreq = "n";
        if (selectedFrequency == weekly) {
            chosenFreq = "w";
        } else if (selectedFrequency == biWeekly) {
            chosenFreq = "b";
        } else if (selectedFrequency == monthly) {
            chosenFreq = "m";
        } else if (selectedFrequency == quarterly) {
            chosenFreq = "q";
        } else if (selectedFrequency == semiAnnually) {
            chosenFreq = "s";
        } else if (selectedFrequency == annually) {
            chosenFreq = "a";
        }

        frequencyInformation.put("frequency", chosenFreq);
        frequencyInformation.put("start_date", "");
        frequencyInformation.put("end_date", "");

        if (!chosenFreq.equals("n")) {
            Date sd = getStartDate();
            Date ed = getEndDate();

            if (sd != null || ed != null) {
                if (sd != null) {
                    frequencyInformation.put("start_date", sdf.format(sd));
                }
                if (ed != null) {
                    frequencyInformation.put("end_date", sdf.format(ed));
                }
            }

            if (allowNextBill && nextBillDateVisible && selectedFrequency != originallySelectedFrequency) {
                Date nb = getNextDate();
                if (nb != null) {
                    frequencyInformation.put("next_bill", sdf.format(nb));
                }
            }
        }

        return frequencyInformation;
    }

    public void disableStartDate() {
        allowStartDate = false;
        startDateTextView.setBackgroundResource(R.drawable.border_5dp_grey_sb);
        startDateTextView.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    public void enableStartDate() {
        allowStartDate = true;
        startDateTextView.setBackgroundResource(R.drawable.border_5dp_ffffff_sb);
        startDateTextView.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    public void disableEndDate() {
        allowEndDate = false;
        endDateTextView.setBackgroundResource(R.drawable.border_5dp_grey_sb);
        endDateTextView.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    public void enableEndDate() {
        allowEndDate = true;
        endDateTextView.setBackgroundResource(R.drawable.border_5dp_ffffff_sb);
        endDateTextView.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    public boolean allowNextBill() {
        return allowNextBill;
    }

    public void setAllowNextBill(boolean allowNextBill) {
        this.allowNextBill = allowNextBill;
    }

    public Date getStartDate() {
        if (startDay > 0 && startMonth > 0 && startYear > 0) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(startYear, startMonth, startDay);
            return myCalendar.getTime();
        }

        return null;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.setTime(startDate);

            startYear = myCalendar.get(Calendar.YEAR);
            startMonth = myCalendar.get(Calendar.MONTH);
            startDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            startDateTextView.setText(sdf.format(startDate));
        }
    }

    public Date getEndDate() {
        if (endDay >= 0 && endMonth >= 0 && endYear >= 0) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(endYear, endMonth, endDay);
            return myCalendar.getTime();
        }

        return null;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.setTime(endDate);

            endYear = myCalendar.get(Calendar.YEAR);
            endMonth = myCalendar.get(Calendar.MONTH);
            endDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            endDateTextView.setText(sdf.format(endDate));
        }
    }

    public Date getNextDate() {
        if (nextDay > 0 && nextMonth > 0 && nextYear > 0) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.set(nextYear, nextMonth, nextDay);
            return myCalendar.getTime();
        }

        return null;
    }

    public void setNextDate(Date nextDate) {
        if (nextDate != null) {
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.setTime(nextDate);

            nextYear = myCalendar.get(Calendar.YEAR);
            nextMonth = myCalendar.get(Calendar.MONTH);
            nextDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            nextDateTextView.setText(sdf.format(nextDate));
        }
    }

    public Button getOriginallySelectedFrequency() {
        return originallySelectedFrequency;
    }

    public void setOriginallySelectedFrequency(Button originallySelectedFrequency) {
        this.originallySelectedFrequency = originallySelectedFrequency;
    }

    public FrequencyPicker(Context context) {
        super(context);
        mContext = context;
    }

    public FrequencyPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0, 0);
    }

    public FrequencyPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr, 0);
    }

    public FrequencyPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.rootView = inflate(mContext, R.layout.widget_frequency_selection, this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        frequencyDateHeightEnd = (int) (90 * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        nextBillDateHeightEnd = (int) (90 * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));

        startDateTextView = rootView.findViewById(R.id.start_date);
        endDateTextView = rootView.findViewById(R.id.end_date);

        oneTime = rootView.findViewById(R.id.frequency_one_time);
        weekly = rootView.findViewById(R.id.frequency_weekly);
        biWeekly = rootView.findViewById(R.id.frequency_bi_weekly);
        monthly = rootView.findViewById(R.id.frequency_monthly);
        quarterly = rootView.findViewById(R.id.frequency_quarterly);
        semiAnnually = rootView.findViewById(R.id.frequency_semi_annually);
        annually = rootView.findViewById(R.id.frequency_annually);

        frequencyDates = rootView.findViewById(R.id.frequency_dates);

        bindRXClicks();
        bindDatePicker();
    }

    public void setFrequencies(ArrayList<String> recurringFrequencies) {
        this.recurringFrequencies = recurringFrequencies;

        oneTime.setVisibility(View.GONE);
        weekly.setVisibility(View.GONE);
        biWeekly.setVisibility(View.GONE);
        monthly.setVisibility(View.GONE);
        quarterly.setVisibility(View.GONE);
        semiAnnually.setVisibility(View.GONE);
        annually.setVisibility(View.GONE);

        for (String freq: recurringFrequencies) {
            if (freq.equals("n")) {
                oneTime.setVisibility(View.VISIBLE);
            } else if (freq.equals("w")) {
                weekly.setVisibility(View.VISIBLE);
            } else if (freq.equals("b")) {
                biWeekly.setVisibility(View.VISIBLE);
            } else if (freq.equals("m")) {
                monthly.setVisibility(View.VISIBLE);
            } else if (freq.equals("q")) {
                quarterly.setVisibility(View.VISIBLE);
            } else if (freq.equals("s")) {
                semiAnnually.setVisibility(View.VISIBLE);
            } else if (freq.equals("a")) {
                annually.setVisibility(View.VISIBLE);
            }
        }
    }

    public void selectFrequency(String freq) {
        if (freq.equals("n")) {
            originallySelectedFrequency = oneTime;
        } else if (freq.equals("w")) {
            originallySelectedFrequency = weekly;
        } else if (freq.equals("b")) {
            originallySelectedFrequency = biWeekly;
        } else if (freq.equals("m")) {
            originallySelectedFrequency = monthly;
        } else if (freq.equals("q")) {
            originallySelectedFrequency = quarterly;
        } else if (freq.equals("s")) {
            originallySelectedFrequency = semiAnnually;
        } else if (freq.equals("a")) {
            originallySelectedFrequency = annually;
        }

        selectFrequency(originallySelectedFrequency);
        if (!freq.equals("n")) toggleFrequencyDates(true);
    }

    public void selectFrequency(Button v) {
        if (selectedFrequency != null) {
            selectedFrequency.setBackgroundResource(R.drawable.border_5dp_ffffff_sb);
            selectedFrequency.setTextColor(getResources().getColor(R.color.text_primary));
        }

        selectedFrequency = v;
        selectedFrequency.setBackgroundResource(R.drawable.border_5dp_4dc7aa_sb);
        selectedFrequency.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * Attach a click listener to the specified view that prevents clicks when {@link FrequencyPicker#animating} is true
     * @param viewId ID of the view the click listener will go to
     * @return Disposable for a filtered click listener attached to the specified view
     */
    private Disposable bindViewToFilter(int viewId){
        final View v = rootView.findViewById(viewId); // Reference to the view being used since RxView.clicks does not provide view
        return RxView.clicks(v) // Begin creating a disposable that responds to the view being clicked
            .filter(o -> !animating) // Prevent view from being clicked when
            .observeOn(AndroidSchedulers.mainThread()) // Listen on the UI thread
            .subscribe(o -> { // Create the subscription
                if (selectedFrequency != v) { // Prevent code from running if the same button was clicked twice
                    FrequencyPicker.this.selectFrequency((Button) v);

                    if (v.getId() == R.id.frequency_one_time) {
                        if (frequencyDatesVisible) {
                            FrequencyPicker.this.toggleFrequencyDates(false);
                        }
                    } else {
                        if (!frequencyDatesVisible) {
                            FrequencyPicker.this.toggleFrequencyDates(true);
                        } else if (allowNextBill) {
                            if (selectedFrequency != originallySelectedFrequency) {
                                // user changed frequency. show next bill input.
                                if (!nextBillDateVisible) {
                                    FrequencyPicker.this.toggleNextBill(true);
                                }
                            } else {
                                // user selected original frequency. hide next bill input.
                                FrequencyPicker.this.toggleNextBill(false);
                            }
                        }
                    }
                }
            });
    }

    private void bindRXClicks() {
        // Add disposables for each button to the main disposable handler
        mDisposables.add(bindViewToFilter(R.id.frequency_one_time));
        mDisposables.add(bindViewToFilter(R.id.frequency_weekly));
        mDisposables.add(bindViewToFilter(R.id.frequency_bi_weekly));
        mDisposables.add(bindViewToFilter(R.id.frequency_monthly));
        mDisposables.add(bindViewToFilter(R.id.frequency_quarterly));
        mDisposables.add(bindViewToFilter(R.id.frequency_semi_annually));
        mDisposables.add(bindViewToFilter(R.id.frequency_annually));
    }

    private void bindDatePicker() {
        Calendar myCalendar = Calendar.getInstance();

        final int y = myCalendar.get(Calendar.YEAR);
        final int m = myCalendar.get(Calendar.MONTH);
        final int d = myCalendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener startDateListener = (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
            startYear = year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            startDateTextView.setText(sdf.format(getStartDate()));
        };

        final DatePickerDialog.OnDateSetListener endDateListener = (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
            endYear = year;
            endMonth = monthOfYear;
            endDay = dayOfMonth;
            endDateTextView.setText(sdf.format(getEndDate()));
        };

        final DatePickerDialog.OnDateSetListener nextDateListener = (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
            nextYear = year;
            nextMonth = monthOfYear;
            nextDay = dayOfMonth;
            nextDateTextView.setText(sdf.format(getNextDate()));
        };

        mDisposables.add(RxView.clicks(rootView.findViewById(R.id.frequency_start))
            .throttleFirst(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((Object o) -> {

                if (allowStartDate) {
                    DatePickerDialog dp;

                    if (startDay >= 0 && startMonth >= 0 && startYear >= 0) {
                        dp = new DatePickerDialog(getContext(), startDateListener, startYear, startMonth, startDay);
                    } else {
                        dp = new DatePickerDialog(getContext(), startDateListener, y, m, d);
                    }

                    dp.getDatePicker().setMinDate(System.currentTimeMillis());

                    if (endDay >= 0 && endMonth >= 0 && endYear >= 0) {
                        dp.getDatePicker().setMaxDate(getEndDate().getTime());
                    }

                    dp.show();
                }
            })
        );

        mDisposables.add(RxView.clicks(rootView.findViewById(R.id.frequency_end))
            .throttleFirst(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((Object o) -> {
                if (allowEndDate) {
                    DatePickerDialog dp;

                    if (endDay >= 0 && endMonth >= 0 && endYear >= 0) {
                        dp = new DatePickerDialog(getContext(), endDateListener, endYear, endMonth, endDay);
                    } else {
                        dp = new DatePickerDialog(getContext(), endDateListener, y, m, d);
                    }

                    if (allowNextBill) {
                        if (nextDay >= 0 && nextMonth >= 0 && nextYear >= 0) {
                            dp.getDatePicker().setMinDate(getNextDate().getTime());
                        } else {
                            dp.getDatePicker().setMinDate(System.currentTimeMillis());
                        }
                    } else {
                        if (startDay >= 0 && startMonth >= 0 && startYear >= 0) {
                            dp.getDatePicker().setMinDate(getStartDate().getTime());
                        } else {
                            dp.getDatePicker().setMinDate(System.currentTimeMillis());
                        }
                    }

                    dp.show();
                }
            })
        );
    }

    private void toggleFrequencyDates(final boolean makeVisible) {
        animating = true;

        int newHeight = 0;
        float alpha = 0.0f;

        if (makeVisible) {
            newHeight = frequencyDateHeightEnd;
            alpha = 1.0f;
        }

        ValueAnimator anim = ValueAnimator.ofInt(frequencyDates.getMeasuredHeight(), newHeight);
        anim.addUpdateListener((ValueAnimator valueAnimator) -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = frequencyDates.getLayoutParams();
            layoutParams.height = val;
            frequencyDates.setLayoutParams(layoutParams);
        });

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(frequencyDates, "alpha", alpha);

        AnimatorSet as = new AnimatorSet();
        as.playTogether(anim, alphaAnim);
        as.setDuration(400);
        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frequencyDatesVisible = makeVisible;
                animating = false;
            }
        });
        as.start();
    }

    private void toggleNextBill(final boolean makeVisible) {
        animating = true;

        int newHeight = 0;
        float alpha = 0.0f;

        if (makeVisible) {
            newHeight = nextBillDateHeightEnd;
            alpha = 1.0f;
            nextBillDate.setVisibility(View.VISIBLE);
        }

        ValueAnimator anim = ValueAnimator.ofInt(nextBillDate.getMeasuredHeight(), newHeight);
        anim.addUpdateListener((ValueAnimator valueAnimator) -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = nextBillDate.getLayoutParams();
            layoutParams.height = val;
            nextBillDate.setLayoutParams(layoutParams);
        });

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(nextBillDate, "alpha", alpha);

        AnimatorSet as = new AnimatorSet();
        as.playTogether(anim, alphaAnim);
        as.setDuration(400);
        as.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                nextBillDateVisible = makeVisible;
                animating = false;
            }
        });
        as.start();
    }

    public void dispose() {
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.dispose();
        }
    }
}
