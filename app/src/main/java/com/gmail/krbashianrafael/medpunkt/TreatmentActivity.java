package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TreatmentActivity extends AppCompatActivity {

    private Window myWindow;

    // id заболеввания
    private int _idDisease = 0;

    // возможность изменять пользователя, показывать стрелку обратно, был ли изменен пользователь
    private static boolean newDisease, goBack, editDisease;

    private ActionBar actionBar;

    // название заболевания
    private static String textDiseaseName = "";
    private static String textTreatment = "";

    // поля названия заболевания, описания лечения и focusHolder
    private TextInputLayout textInputLayoutDiseaseName;
    private TextInputEditText editTextDiseaseName;
    private EditText focusHolder;

    // это кастомный EditText у которого клавиатура не перекрывает текст
    private static boolean hasEditTextMaxHeight;
    private static int editTextMaxHeightPx;
    private TextView txtTitleTreatmen;
    private MyEditText editTextTreatment;

    // TextView добавления фотоснимка лечения
    TextView textViewAddTreatmentPhoto;

    // RecyclerView для фотоснимков лечения
    RecyclerView recyclerTreatmentPhotos;

    // fab
    private FloatingActionButton fab;

    // Animation fabHideAnimation
    private Animation fabHideAnimation;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;

    // элемент меню "сохранить"
    private TextView menuItemSaveView;

    // Animation saveShowAnimation
    private Animation saveShowAnimation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        myWindow = getWindow();
        myWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();

        _idDisease = intent.getIntExtra("_idDisease", 0);

        if (intent.hasExtra("diseaseName")) {
            textDiseaseName = intent.getStringExtra("diseaseName");
        }

        if (intent.hasExtra("textTreatment")) {
            textTreatment = intent.getStringExtra("textTreatment");
        }

        if (intent.hasExtra("editDisease")) {
            editDisease = intent.getBooleanExtra("editDisease", false);
        }

        if (intent.hasExtra("newDisease")) {
            newDisease = intent.getBooleanExtra("newDisease", false);
        }

        textInputLayoutDiseaseName = findViewById(R.id.text_input_layout_disease_name);
        editTextDiseaseName = findViewById(R.id.editText_disease_name);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_30dp);
            actionBar.setElevation(0);

            if (!textDiseaseName.equals("")) {
                actionBar.setTitle(textDiseaseName);
                editTextDiseaseName.setText(textDiseaseName);
            }
        }

        editTextDiseaseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutDiseaseName.setErrorEnabled(false);
                }
            }
        });

        focusHolder = findViewById(R.id.focus_holder);
        focusHolder.requestFocus();

        Resources r = getResources();
        int screenHeightDp = r.getConfiguration().screenHeightDp;
        final int[] editTextTreatmentMaxHeight = {(int) (screenHeightDp / 1.2)};

        editTextTreatment = findViewById(R.id.editTextTreatment);
        editTextTreatment.setText(textTreatment);
        editTextTreatment.setMaxHeight(editTextTreatmentMaxHeight[0]);

        textViewAddTreatmentPhoto = findViewById(R.id.textViewAddTreatmentPhoto);

        recyclerTreatmentPhotos = findViewById(R.id.recycler_treatment_photos);

        // анимация для элемента меню "сохранить"
        saveShowAnimation = AnimationUtils.loadAnimation(this, R.anim.save_show);

        fab = findViewById(R.id.fabEditTreatment);

        fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);
        fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }
        });

        txtTitleTreatmen = findViewById(R.id.txt_title_treatmen);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.startAnimation(fabHideAnimation);

                textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                editTextDiseaseName.setEnabled(true);

                editDisease = false;

                invalidateOptionsMenu();

                editTextTreatment.setFocusable(true);
                editTextTreatment.setFocusableInTouchMode(true);
                editTextTreatment.setCursorVisible(true);
                editTextTreatment.setSelection(editTextTreatment.getText().toString().length());
                editTextTreatment.requestFocus();

                // выдвигаем клавиатуру
                //Log.d("onReceiveResult", " befor showSoftInput");

                View viewToShow = TreatmentActivity.this.getCurrentFocus();

                //MyResultReceiver myResultReceiver = new MyResultReceiver(null);

                if (viewToShow != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        Log.d("screenHeight", " viewToShow ");
                        imm.showSoftInput(viewToShow, 0);
                    }
                    else {
                        Log.d("screenHeight", " view Null ");
                    }
                }

                //myResultReceiver = null;

                //Log.d("onReceiveResult", " after showSoftInput");

                //if (!hasEditTextMaxHeight) {

                // проверить показана ли клавиатура

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Resources r = getResources();
                        int screenHeightDp = r.getConfiguration().screenHeightDp;
                        int editTextMaxHeightDp = (int) (screenHeightDp / 1.88);
                        //editTextMaxHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextMaxHeightDp, r.getDisplayMetrics());

                        int screenHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenHeightDp, r.getDisplayMetrics());

                        Log.d("screenHeight", " screenHeightDp = " + screenHeightDp);
                        Log.d("screenHeight", " editTextMaxHeightDp = " + editTextMaxHeightDp);
                        Log.d("screenHeight", " screenHeightPx = " + screenHeightPx);
                        //Log.d("screenHeight", " editTextMaxHeightPx = " + editTextMaxHeightPx);

                        Window mRootWindow = getWindow();
                        final Rect rect = new Rect();
                        View rootView = mRootWindow.getDecorView();
                        rootView.getWindowVisibleDisplayFrame(rect);

                        int keyBoardHeight = screenHeightPx - rect.bottom;

                        Log.d("screenHeight", " rect.top = " + rect.top);
                        Log.d("screenHeight", " rect.bottom = " + rect.bottom);
                        Log.d("screenHeight", " keyBoardHeight = " + keyBoardHeight);



                        //final int[] editTextTreatmentMaxHeight = {300};
                        editTextTreatmentMaxHeight[0] = rect.bottom - rect.top - txtTitleTreatmen.getHeight();

                        Log.d("screenHeight", " txtTitleTreatmen.getHeight() = " + txtTitleTreatmen.getHeight());
                        Log.d("screenHeight", " editTextTreatmentMaxHeight new = " + editTextTreatmentMaxHeight[0]);



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editTextTreatment.setMaxHeight(editTextTreatmentMaxHeight[0]);
                                hasEditTextMaxHeight = true;
                            }
                        });

                    }
                }).start();
*/
                //}

            }
        });

        // это фиктивное фото заболевания
        final LinearLayout recyclerTreatmentPhotoItem = findViewById(R.id.recycler_treatment_photo_item);
        recyclerTreatmentPhotoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textDiseaseName = editTextDiseaseName.getText().toString().trim();
                textTreatment = editTextTreatment.getText().toString();

                Intent intentToTreatmentPhoto = new Intent(TreatmentActivity.this, FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("_idDisease", 2);
                intentToTreatmentPhoto.putExtra("treatmentPhotoUri", getString(R.string.path_to_treatment_photo));
                intentToTreatmentPhoto.putExtra("textPhotoDescription", "Рентген");
                intentToTreatmentPhoto.putExtra("textDateOfTreatmentPhoto", "01.02.2018 ");
                startActivity(intentToTreatmentPhoto);
            }
        });

        final FrameLayout dividerFrameGray = findViewById(R.id.divider_frame_gray);

        // добавление фото
        textViewAddTreatmentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textDiseaseName = editTextDiseaseName.getText().toString().trim();
                textTreatment = editTextTreatment.getText().toString();

                Intent intentToTreatmentPhoto = new Intent(TreatmentActivity.this, FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);

            }
        });

        if (newDisease) {
            editTextDiseaseName.requestFocus();
            editTextDiseaseName.setSelection(editTextDiseaseName.getText().toString().length());
        }

        if (editDisease) {
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            editTextTreatment.setFocusable(false);
            editTextTreatment.setFocusableInTouchMode(false);
            editTextTreatment.setCursorVisible(false);
            focusHolder.requestFocus();

            fab.startAnimation(fabShowAnimation);
        }
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        //fab.performClick();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                View viewToShow = myWindow.getCurrentFocus();
                if (viewToShow != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        Log.d("screenHeight", " viewToShow ");
                        imm.showSoftInput(viewToShow, 0);
                    }
                }

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final Rect rect = new Rect();
                View rootView = myWindow.getDecorView();
                rootView.getWindowVisibleDisplayFrame(rect);

                Log.d("screenHeight", " rect.bottom = " + rect.bottom);
                Log.d("screenHeight", " rect.top = " + rect.top);
                Log.d("screenHeight", " ect.bottom - rect.top = " + (rect.bottom - rect.top - txtTitleTreatmen.getHeight()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextTreatment.setMaxHeight(rect.bottom - rect.top - txtTitleTreatmen.getHeight());
                    }
                });

                hideSoftInput();

            }
        }).start();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_treatment_fullphoto, menu);

        menu.removeItem(R.id.action_delete);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                getResources().getString(R.string.delete_disease)));

        return true;
    }

    // SpannableString с картикной для элеменов меню
    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // если в состоянии edit (тоесть есть кнопка fab со значком редактирования)
        // то в меню элемент "сохранить" делаем не видимым
        // видимым остается "удалить"
        if (editDisease) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {

            // иначе, делаем невидимым "удалить"
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemDelete.setVisible(false);

            // и создаем ActionView на основе элемента меню "сохранить" для применени анимации save_show
            // т.к. в menu_user_treatment_fullphoto элемент "сохранить" имеет атрибут
            // app:actionViewClass="android.widget.TextView"
            // то menuItemSave.getActionView() возвращает TextView
            // с которым и проделываем дальнейшие трансформации:
            // устанавливаем текст, размер шрифта, цвет шрифта, анимацию и слушатель нажатия
            // текст берем из R.string.save, где присутствует юникодовский пробел \u2000
            // иначе после слова "сохранить" обычные пробелы автоматически убираются
            // и слово вплотную прилегает к краю экрана
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSaveView = (TextView) menuItemSave.getActionView();
            menuItemSaveView.setText(R.string.save);
            menuItemSaveView.setTextSize(18f);
            menuItemSaveView.setTextColor(getResources().getColor(R.color.colorAccentThird));

            menuItemSaveView.startAnimation(saveShowAnimation);

            menuItemSaveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // скручиваем клавиатуру
                    hideSoftInput();

                    if (diseaseAndTreatmentHasNotChanged() && !newDisease) {
                        editDisease = true;
                        textInputLayoutDiseaseName.setVisibility(View.GONE);
                        editTextTreatment.setSelection(0);
                        editTextTreatment.setFocusable(false);
                        editTextTreatment.setFocusableInTouchMode(false);
                        editTextTreatment.setCursorVisible(false);

                        focusHolder.requestFocus();

                        invalidateOptionsMenu();
                        fab.startAnimation(fabShowAnimation);

                    } else {
                        saveDiseaseAndTreatment();

                        focusHolder.requestFocus();
                    }
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:

                hideSoftInput();

                // Если не было изменений
                if (diseaseAndTreatmentHasNotChanged()) {
                    goToDiseasesActivity();
                    finish();
                    return true;
                }

                textInputLayoutDiseaseName.setError(null);

                // Если были изменения
                // если выходим без сохранения изменений
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToDiseasesActivity();
                            }
                        };

                // если выходим с сохранением изменений
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            case R.id.action_delete:
                hideSoftInput();
                deleteDiseaseAndTreatmentFromDataBase();
                return true;

            default:
                super.onOptionsItemSelected(item);
                finish();
                return true;
        }
    }

    @Override
    public void onBackPressed() {

        hideSoftInput();

        if (diseaseAndTreatmentHasNotChanged()) {
            super.onBackPressed();
            finish();
            return;
        }

        textInputLayoutDiseaseName.setError(null);

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Диалог "сохранить или выйти без сохранения"
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack = true;

                if (dialog != null) {
                    dialog.dismiss();
                }

                saveDiseaseAndTreatment();
            }
        });

        builder.setPositiveButton(R.string.no, discardButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveDiseaseAndTreatment() {

        focusHolder.requestFocus();

        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        // првоерка имени
        String nameToCheck = editTextDiseaseName.getText().toString().trim();

        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutDiseaseName.setError(getString(R.string.error_disease_name));
            focusHolder.requestFocus();
            //editTextDiseaseName.requestFocus();
            editTextDiseaseName.startAnimation(scaleAnimation);

            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        // проверка окончена, начинаем сохранение

        // присваиваем стрингам textDiseaseName и textTreatment значения полей editTextDiseaseName и editTextTreatment
        // для дальнейшей проверки на их изменения
        textDiseaseName = nameToCheck;
        textTreatment = editTextTreatment.getText().toString();

        if (actionBar != null) {
            actionBar.setTitle(textDiseaseName);
        }

        // когда сохраняем НОВОЕ заболевание получаем его _id
        // в данном случае присваиваем фейковый _idDisease = 2

        _idDisease = 2;

        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
        if (newDisease) {

            newDisease = false;
            saveDiseaseAndTreatmentToDataBase();

            // если была нажата стрелка "обратно" - идем обратно
            if (goBack) {
                goToDiseasesActivity();
            } else {
                editDisease = true;
                textInputLayoutDiseaseName.setVisibility(View.GONE);
                editTextTreatment.setSelection(0);
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);
                //textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

                invalidateOptionsMenu();
                fab.startAnimation(fabShowAnimation);
            }
        }
        // если НЕ новый пользователь, то обновляем в базу и
        else {
            updateDiseaseAndTreatmentToDataBase();

            // если была нажата стрелка "обратно" - идем обратно
            if (goBack) {
                goToDiseasesActivity();
            } else {
                editDisease = true;
                textInputLayoutDiseaseName.setVisibility(View.GONE);
                editTextTreatment.setSelection(0);
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);
                //textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

                invalidateOptionsMenu();
                fab.startAnimation(fabShowAnimation);
            }
        }
    }

    // проверка на изменения заболевания
    private boolean diseaseAndTreatmentHasNotChanged() {
        return editTextDiseaseName.getText().toString().equals(textDiseaseName) &&
                editTextTreatment.getText().toString().equals(textTreatment);
    }

    private void goToDiseasesActivity() {
        Intent intent = new Intent(TreatmentActivity.this, DiseasesActivity.class);
        startActivity(intent);

        finish();
    }

    private void hideSoftInput() {
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            Log.d("screenHeight", " viewToHide ");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }

    private void saveDiseaseAndTreatmentToDataBase() {
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TreatmentActivity.this, "DiseaseAndTreatment Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDiseaseAndTreatmentToDataBase() {
        //TODO реализовать обновление пользователя в базу
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TreatmentActivity.this, "DiseaseAndTreatment Updated To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteDiseaseAndTreatmentFromDataBase() {
        //TODO реализовать удаление пользователя из базы
        Toast.makeText(this, "DiseaseAndTreatment Deleted from DataBase", Toast.LENGTH_LONG).show();
    }

   /* private class MyResultReceiver extends ResultReceiver {

        Handler mHandler;

        public MyResultReceiver(Handler handler) {
            super(handler);

            mHandler = handler;
        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            final Rect rect = new Rect();
            View rootView = findViewById(R.id.root_view);
            rootView.getWindowVisibleDisplayFrame(rect);

            Log.d("onReceiveResult", " rect.bottom - rect.top = " + (rect.bottom - rect.top));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editTextTreatment.setMaxHeight(rect.bottom - rect.top - 41);
                }
            });

        }

    }*/

}


