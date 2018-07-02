package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

public class TreatmentActivity extends AppCompatActivity {

    // Фрагменты
    protected TreatmentDescriptionFragment treatmentDescriptionFragment;
    protected TreatmentPhotosFragment treatmentPhotosFragment;

    // id заболеввания
    private int _idDisease = 2;

    // возможность изменять пользователя, показывать стрелку обратно, был ли изменен пользователь
    private boolean goBack, newDisease;
    protected boolean editDisease;

    // это временно для отработки в treatmentPhotoRecyclerView пустого листа
    protected boolean tempNewDisease;

    private ActionBar actionBar;

    // название заболевания
    private String textDiseaseName = "";
    protected String textTreatment = ""; //private static

    // поля названия заболевания, описания лечения и focusHolder
    protected TextInputLayout textInputLayoutDiseaseName;
    protected TextInputEditText editTextDiseaseName;
    private EditText focusHolder;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;

    protected ViewPager viewPager;

    protected TreatmentAdapter categoryAdapter;

    protected TabLayout tabLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();

        _idDisease = intent.getIntExtra("_idDisease", 2);

        if (intent.hasExtra("diseaseName")) {
            textDiseaseName = intent.getStringExtra("diseaseName");
        }

        if (intent.hasExtra("textTreatment")) {
            textTreatment = intent.getStringExtra("textTreatment");
        }

        editDisease = intent.getBooleanExtra("editDisease", false);

        newDisease = intent.getBooleanExtra("newDisease", false);

        // это временно для отработки в treatmentPhotoRecyclerView пустого листа
        tempNewDisease = newDisease;

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

        // анимация для показа fabEditTreatmentDescripton
        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }
        });

        categoryAdapter = new TreatmentAdapter(this, getSupportFragmentManager());

        viewPager = findViewById(R.id.viewpager);

        tabLayout = findViewById(R.id.tabs);

        if (newDisease) {
            editTextDiseaseName.requestFocus();
            editTextDiseaseName.setSelection(0);
            categoryAdapter.setPagesCount(1);
            tabLayout.setVisibility(View.GONE);
        } else {
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            focusHolder.requestFocus();
        }

        viewPager.setAdapter(categoryAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // при нажатии на табы формируем внешний вид табов
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_white_24dp),
                            getResources().getString(R.string.description)));

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_white_24dp),
                            getResources().getString(R.string.photos)));
                }

                tabLayout.setTabTextColors(getResources().getColor(R.color.black_overlay),
                        getResources().getColor(android.R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // при отжатии табов формируем внешний вид табов
                if (tab.getPosition() == 0) {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_gray_24dp),
                            getResources().getString(R.string.description)));

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_gray_24dp),
                            getResources().getString(R.string.photos)));
                }

                tabLayout.setTabTextColors(getResources().getColor(R.color.black_overlay),
                        getResources().getColor(android.R.color.white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // при повторном нажатии на таб "ОПИСАНИЕ" прокручиваем вверх описание лечения
                    treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                    treatmentDescriptionFragment.editTextTreatment.requestFocus();
                    treatmentDescriptionFragment.editTextTreatment.setSelection(0);

                    treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    focusHolder.requestFocus();

                } else {
                    // при повторном нажатии на таб "СНИМКИ" прокручиваем вверх список снимков
                    treatmentPhotosFragment.recyclerTreatmentPhotos.smoothScrollToPosition(0);
                }
            }
        });
    }

    // инициализация Фрагментов если они null
    // вызов этого метода и проверка происходит в самих фрагментах
    protected void initTreatmentDescriptionFragment() {
        treatmentDescriptionFragment = (TreatmentDescriptionFragment) getSupportFragmentManager().getFragments().get(0);
    }

    protected void initTreatmentPhotosFragment() {
        treatmentPhotosFragment = (TreatmentPhotosFragment) getSupportFragmentManager().getFragments().get(1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

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

        // если в состоянии Не edit (тоесть, есть кнопка fabEditTreatmentDescripton со значком редактирования)
        // то в меню элемент "сохранить" делаем не видимым
        // видимым остается "удалить"
        if (!editDisease) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {
            // иначе, делаем невидимым "удалить"
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemDelete.setVisible(false);
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

                hideSoftInput();

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
            case R.id.action_save:
                // скручиваем клавиатуру
                hideSoftInput();

                if (diseaseAndTreatmentHasNotChanged() && !newDisease) {

                    // делаем два листа в адаптере
                    categoryAdapter.setPagesCount(2);
                    viewPager.setAdapter(categoryAdapter);

                    editDisease = false;
                    textInputLayoutDiseaseName.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);

                    treatmentDescriptionFragment.editTextTreatment.requestFocus();
                    treatmentDescriptionFragment.editTextTreatment.setSelection(0);
                    treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

                    focusHolder.requestFocus();

                    // обновляем OptionsMenu
                    invalidateOptionsMenu();

                    treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
                } else {
                    saveDiseaseAndTreatment();

                    focusHolder.requestFocus();
                }

                return true;
            case R.id.action_delete:
                hideSoftInput();
                deleteDiseaseAndTreatmentFromDataBase(_idDisease);
                return true;

            default:
                super.onOptionsItemSelected(item);
                finish();
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (diseaseAndTreatmentHasNotChanged()) {
            super.onBackPressed();
            finish();
            return;
        }

        hideSoftInput();

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
            editTextDiseaseName.startAnimation(scaleAnimation);

            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        // проверка окончена, начинаем сохранение

        // когда сохраняем НОВОЕ заболевание получаем его _id
        // в данном случае присвоенно фейковое знаяение _idDisease = 2

        // если было нажато идти обратно
        if (goBack) {
            if (newDisease) {
                // сохранять в базу в отдельном треде
                saveDiseaseAndTreatmentToDataBase(_idDisease);
            } else {
                // обновлять в базу в отдельном треде
                updateDiseaseAndTreatmentToDataBase(_idDisease);
            }

            //и идем в DiseasesActivity
            goToDiseasesActivity();

        } else {
            if (newDisease) {
                // т.к. сохраняем новое заболевание,
                // то оно уже не newDisease
                newDisease = false;

                // сохранять в базу в отдельном треде
                saveDiseaseAndTreatmentToDataBase(_idDisease);
            } else {
                // обновлять в базу в отдельном треде
                updateDiseaseAndTreatmentToDataBase(_idDisease);
            }

            // и формируем UI
            categoryAdapter.setPagesCount(2);
            viewPager.setAdapter(categoryAdapter);

            // присваиваем стрингам textDiseaseName и textTreatment значения полей editTextDiseaseName и editTextTreatment
            // для дальнейшей проверки на их изменения
            textDiseaseName = nameToCheck;
            textTreatment = treatmentDescriptionFragment.editTextTreatment.getText().toString();

            if (actionBar != null) {
                actionBar.setTitle(textDiseaseName);
            }

            editDisease = false;
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);

            treatmentDescriptionFragment.editTextTreatment.setSelection(0);
            treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
            treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
            treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

            focusHolder.requestFocus();

            invalidateOptionsMenu();

            treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
        }
    }

    // проверка на изменения заболевания
    private boolean diseaseAndTreatmentHasNotChanged() {
        return editTextDiseaseName.getText().toString().equals(textDiseaseName) &&
                treatmentDescriptionFragment.editTextTreatment.getText().toString().equals(textTreatment);
    }

    private void goToDiseasesActivity() {
        finish();
    }

    private void hideSoftInput() {
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }

    private void saveDiseaseAndTreatmentToDataBase(int _idDisease) {
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TreatmentActivity.this, "DiseaseAndTreatment Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDiseaseAndTreatmentToDataBase(int _idDisease) {
        //TODO реализовать обновление пользователя в базу
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TreatmentActivity.this, "DiseaseAndTreatment Updated To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteDiseaseAndTreatmentFromDataBase(int _idDisease) {
        //TODO реализовать удаление пользователя из базы
        Toast.makeText(this, "DiseaseAndTreatment Deleted from DataBase", Toast.LENGTH_LONG).show();

        finish();
    }
}