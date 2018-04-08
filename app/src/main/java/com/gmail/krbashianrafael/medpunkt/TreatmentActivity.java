package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    // id заболеввания
    private int _id_disease = 0;

    // возможность изменфть пользователя, показывать стрелку обратно, был ли изменен пользователь
    private boolean newDisease, goBack, editDisease;

    private ActionBar actionBar;

    // название заболевания
    private String textDiseaseName = "";
    private String textTreatment = "";

    // поля названия заболевания, описания лечения и focusHolder
    private TextInputLayout textInputLayoutDiseaseName;
    private TextInputEditText editTextDiseaseName;
    private EditText editTextTreatment, focusHolder;

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
    TextView menuItemSaveView;

    // Animation saveShowAnimation
    private Animation saveShowAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        Intent intent = getIntent();

        _id_disease = intent.getIntExtra("_id_disease", 0);
        textDiseaseName = intent.getStringExtra("diseaseName");
        editDisease = intent.getBooleanExtra("editDisease", false);
        newDisease = intent.getBooleanExtra("newDisease", false);

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        textInputLayoutDiseaseName = findViewById(R.id.text_input_layout_disease_name);
        editTextDiseaseName = findViewById(R.id.editText_disease_name);
        if (newDisease) {
            editTextDiseaseName.requestFocus();
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);
            actionBar.setElevation(0);

            if (textDiseaseName != null) {
                actionBar.setTitle(textDiseaseName);
                editTextDiseaseName.setText(textDiseaseName);
            } else {
                textDiseaseName = "";
            }
        }

        editTextDiseaseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutDiseaseName.setErrorEnabled(false);
                    //textInputLayoutDiseaseName.setError(null);
                }
            }
        });

        focusHolder = findViewById(R.id.focus_holder);
        editTextTreatment = findViewById(R.id.editTextTreatment);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(fabHideAnimation);

                textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                editTextDiseaseName.setEnabled(true);
                editTextTreatment.setFocusable(true);
                editTextTreatment.setFocusableInTouchMode(true);
                editTextTreatment.setCursorVisible(true);
                editTextTreatment.requestFocus();
                textViewAddTreatmentPhoto.setVisibility(View.VISIBLE);

                editDisease = false;

                invalidateOptionsMenu();
            }
        });

        // это фиктивное фото заболевания
        final LinearLayout recyclerTreatmentPhotoItem = findViewById(R.id.recycler_treatment_photo_item);
        recyclerTreatmentPhotoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToTreatmentPhoto = new Intent(TreatmentActivity.this, TreatmentPhotoActivity.class);
                startActivity(intentToTreatmentPhoto);
            }
        });

        final FrameLayout dividerFrameGray = findViewById(R.id.divider_frame_gray);

        // добавление фиктивного фото
        textViewAddTreatmentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // скручиваем клавиатуру
                hideSoftInput();

                recyclerTreatmentPhotoItem.setVisibility(View.VISIBLE);
                dividerFrameGray.setVisibility(View.VISIBLE);
            }
        });

        if (editDisease) {
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            editTextTreatment.setFocusable(false);
            editTextTreatment.setFocusableInTouchMode(false);
            editTextTreatment.setCursorVisible(false);
            textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

            fab.startAnimation(fabShowAnimation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_treatment, menu);

        menu.removeItem(R.id.action_delete_disease);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete_disease, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
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
            // т.к. в menu_user элемент "сохранить" имеет атрибут
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
                    if (diseaseAndTreatmentHasNotChanged() && !newDisease) {
                        // скручиваем клавиатуру
                        hideSoftInput();

                        focusHolder.requestFocus();

                        editDisease = true;
                        textInputLayoutDiseaseName.setVisibility(View.GONE);
                        editTextTreatment.setFocusable(false);
                        editTextTreatment.setFocusableInTouchMode(false);
                        editTextTreatment.setCursorVisible(false);
                        textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

                        invalidateOptionsMenu();
                        fab.startAnimation(fabShowAnimation);

                    } else {

                        // скручиваем клавиатуру
                        hideSoftInput();

                        saveDiseaseAndTreatment();

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
                // Если не было изменений
                if (diseaseAndTreatmentHasNotChanged()) {
                    goToDiseasesActivity();
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

            case R.id.action_delete_disease:
                deleteDiseaseAndTreatmentFromDataBase();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (diseaseAndTreatmentHasNotChanged()) {
            super.onBackPressed();
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
                saveDiseaseAndTreatment();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton(R.string.no, discardButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveDiseaseAndTreatment() {
        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        String nameToCheck = editTextDiseaseName.getText().toString().trim();

        // првоерка имени
        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutDiseaseName.setError(getString(R.string.error_disease_name));
            focusHolder.requestFocus();
            //editTextDiseaseName.requestFocus();
            editTextDiseaseName.startAnimation(scaleAnimation);

            // показываем клавиатуру
            /*if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
            }*/

            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        // проверка окончена, начинаем сохранение

        focusHolder.requestFocus();

        textDiseaseName = nameToCheck;

        actionBar.setTitle(textDiseaseName);

        // когда сохраняем НОВОЕ заболевание получаем его _id
        // в данном случае присваиваем фейковый _id = 1

        _id_disease = 1;

        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
        if (newDisease) {
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
                textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

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
                textViewAddTreatmentPhoto.setVisibility(View.INVISIBLE);

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
        //intent.putExtra("_idUser", _idUser);
        //intent.putExtra("UserName", textUserName);
        //intent.putExtra("birthDate", textUserBirthDate);
        //intent.putExtra("userPhotoUri", userPhotoUri);
        startActivity(intent);
    }

    private void hideSoftInput(){
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
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
}
