package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixCorrector;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.squareup.picasso.Picasso;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenPhotoActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private View mDescriptionView;
    private FloatingActionButton fab;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

            fab.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible, goBack, editTreatmentPhoto, newTreatmentPhoto, landscape;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    // элемент меню "сохранить"
    private TextView menuItemSaveView;

    // ActionBar actionBar
    private ActionBar actionBar;

    private EditText focusHolder;
    private EditText textDate;
    private TextInputLayout textInputLayoutPhotoDescription;
    private TextInputEditText editTextPhotoDescription;
    private String textPhotoDescription;

    // screenHeightDp, screenWidthDp
    int myScreenHeightPx, myScreenWidthPx;

    // путь к загружаемому фото
    private Uri selectedImage;

    // фото
    private ImageView imagePhoto;

    // угол поворота фотографии
    private float rotate = 0;

    // zoom
    MyImageMatrixTouchHandler imageMatrixTouchHandler;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Resources r = getResources();
        int myScreenHeightDp = r.getConfiguration().screenHeightDp;
        myScreenHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myScreenHeightDp, r.getDisplayMetrics());

        int myScreenWidthDp = r.getConfiguration().screenWidthDp;
        myScreenWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myScreenWidthDp, r.getDisplayMetrics());

        Intent intent = getIntent();

        //_id_disease = intent.getIntExtra("_id_disease", 0);
        textPhotoDescription = intent.getStringExtra("textPhotoDescription");
        editTreatmentPhoto = intent.getBooleanExtra("editTreatmentPhoto", false);
        newTreatmentPhoto = intent.getBooleanExtra("newTreatmentPhoto", false);

        focusHolder = findViewById(R.id.focus_holder);

        textDate = findViewById(R.id.editText_date);
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftInput();

                // выбираем дату фото
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        textInputLayoutPhotoDescription = findViewById(R.id.text_input_layout_photo_description);
        // чтоб textInputLayoutPhotoDescription не реагировал на ClickL
        textInputLayoutPhotoDescription.setOnClickListener(null);

        editTextPhotoDescription = findViewById(R.id.editText_photo_description);
        // при editTextPhotoDescription OnFocus убираем ошибку
        editTextPhotoDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutPhotoDescription.setErrorEnabled(false);
                }
            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);
            actionBar.setElevation(0);

            if (textPhotoDescription != null) {
                actionBar.setTitle(textPhotoDescription);
                editTextPhotoDescription.setText(textPhotoDescription);
            } else {
                textPhotoDescription = "";
            }
        }

        mVisible = true;
        mDescriptionView = findViewById(R.id.fullscreen_content_description);
        imagePhoto = findViewById(R.id.fullscreen_image);

        // zoomer
        imageMatrixTouchHandler = new MyImageMatrixTouchHandler(this);
        imagePhoto.setOnTouchListener(imageMatrixTouchHandler);

        fab = findViewById(R.id.fabEditTreatmentPhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionView.setVisibility(View.VISIBLE);
                textDate.setVisibility(View.VISIBLE);
                editTextPhotoDescription.requestFocus();
                editTextPhotoDescription.setSelection(editTextPhotoDescription.getText().toString().length());
                fab.setVisibility(View.GONE);
                editTreatmentPhoto = true;

                imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

                invalidateOptionsMenu();
            }
        });

        if (editTreatmentPhoto) {
            mDescriptionView.setVisibility(View.VISIBLE);
            textDate.setVisibility(View.VISIBLE);
            editTextPhotoDescription.requestFocus();
            editTextPhotoDescription.setSelection(editTextPhotoDescription.getText().toString().length());
            fab.setVisibility(View.GONE);
        } else {
            mDescriptionView.setVisibility(View.GONE);
            textDate.setVisibility(View.GONE);
        }

        // если было нажато добваить фото
        // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
        if (newTreatmentPhoto){
            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение на чтение и запись фото
                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hide();
            landscape = true;
            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagePhoto.startAnimation(AnimationUtils.loadAnimation(FullscreenPhotoActivity.this, R.anim.scale_in_scale_out));

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            show();
            landscape = false;
            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagePhoto.startAnimation(AnimationUtils.loadAnimation(FullscreenPhotoActivity.this, R.anim.scale_in_scale_out));
        }
    }

    // результат запроса на загрузку фото
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // TODO userHasChangedPhoto = true;

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            } else {
                Snackbar.make(imagePhoto, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // здесь грузим фотку в imagePhoto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();

            if (selectedImage != null) {

                rotate = getRotation(this, selectedImage);

                Picasso.get().load(selectedImage).
                        placeholder(R.color.colorAccent).
                        error(R.color.colorAccentSecondary).
                        resize(myScreenWidthPx, myScreenHeightPx).
                        rotate(rotate).
                        centerInside().
                        into(imagePhoto);
            }

            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // TODO реализовать сохранение фото
            // SystemClock.elapsedRealtime(); - для нумерации сохраняемых файлов
            // для экстернал
            // String root = Environment.getExternalStorageDirectory().toString();
            // File myDir = new File(root + "/Medpunkt/users_photos");
            // при этом получится File imgFile = new File("/storage/emulated/0/Medpunkt/users_photos/Image-1.jpg");

            // для интернал
                /*
                String root = getFilesDir().toString();
                File myDir = new File(root + "/treatment_photos");

                if (!myDir.mkdirs()) {
                    Log.d("myDir.mkdirs", "users_photos_dir_Not_created");
                }

                String fileName = "Image-" + 2 + ".jpg";
                final File file = new File(myDir, fileName);

                //Log.d("file", "file = " + file);
                // при этом путь к файлу
                // получается: /data/data/com.gmail.krbashianrafael.medpunkt/files/treatment_photos/Image-2.jpg

                // заменяем файл удалением, т.к. у юзера бдует тольок одно фото
                if (file.exists()) {
                    if (!file.delete()) {
                        Toast.makeText(FullscreenPhotoActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                    }
                }

                new Thread(new Runnable() {
                    FileOutputStream outputStream;

                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = Picasso.get().load(selectedImage).
                                    resize(myScreenHeightDp, myScreenWidthDp).
                                    centerInside().
                                    get();
                            outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ).start();
                */

        }
        // если не выбрали фото идем обратно
        else {
            if (newTreatmentPhoto) {
                goToTreatmentActivity();
            }
        }
    }

    // метод для получения оринетации (угол поворота) фотографии
    // т.к. Picasso все фото вставляет боком, то нужно поворачивать на нужный угол
    private int getRotation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }
            cursor.moveToFirst();
        }

        int orientation = 0;
        if (cursor != null) {
            orientation = cursor.getInt(0);
            cursor.close();
            cursor = null;
        }

        return orientation;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        if (!editTreatmentPhoto) {
            delayedHide(100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_treatment_fullphoto, menu);

        menu.removeItem(R.id.action_delete);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                getResources().getString(R.string.delete_treatment_photo)));

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
        if (!editTreatmentPhoto) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {

            // иначе, делаем невидимым "удалить"
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemDelete.setVisible(false);

            MenuItem menuItemSave = menu.getItem(1);
            menuItemSaveView = (TextView) menuItemSave.getActionView();
            menuItemSaveView.setText(R.string.save);
            menuItemSaveView.setTextSize(18f);
            menuItemSaveView.setTextColor(getResources().getColor(R.color.colorAccentThird));

            menuItemSaveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    if (photoDescriptionHasNotChanged() && !newTreatmentPhoto) {

                        hideSoftInput();

                        editTreatmentPhoto = false;
                        mDescriptionView.setVisibility(View.GONE);
                        textDate.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                    } else {
                        saveTreatmentPhoto();
                    }
                }
            });
        }

        return true;
    }

    public void toggle() {

        imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (!editTreatmentPhoto) {

            imagePhoto.startAnimation(AnimationUtils.loadAnimation(FullscreenPhotoActivity.this, R.anim.scale_in_scale_out));

            if (mVisible) {
                hide();
            } else {
                show();
            }

        } else {
            // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение на чтение и запись фото
                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        }
    }

    private void hide() {
        // Hide UI first
        if (actionBar != null) {
            actionBar.hide();
        }
        mDescriptionView.setVisibility(View.GONE);
        textDate.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        if (editTreatmentPhoto) {
            fab.setVisibility(View.INVISIBLE);
            mDescriptionView.setVisibility(View.VISIBLE);
            textDate.setVisibility(View.VISIBLE);

        } else {
            fab.setVisibility(View.VISIBLE);
            mDescriptionView.setVisibility(View.INVISIBLE);
            textDate.setVisibility(View.INVISIBLE);
        }

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // Если не было изменений
                if (photoDescriptionHasNotChanged()) {
                    goToTreatmentActivity();
                    return true;
                }

                // Если были изменения
                // если выходим без сохранения изменений
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToTreatmentActivity();
                            }
                        };

                // если выходим с сохранением изменений
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            case R.id.action_delete:
                deleteTreatmentPhotoFromDataBase();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (photoDescriptionHasNotChanged()) {
            super.onBackPressed();
            return;
        }

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
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {

        hideSoftInput();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                goBack = true;

                if (dialog != null) {
                    dialog.dismiss();
                }

                saveTreatmentPhoto();
            }
        });

        builder.setPositiveButton(R.string.no, discardButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // проверка на изменения описания фото
    private boolean photoDescriptionHasNotChanged() {
        return editTextPhotoDescription.getText().toString().equals(textPhotoDescription);
    }

    private void goToTreatmentActivity() {
        Intent intent = new Intent(FullscreenPhotoActivity.this, TreatmentActivity.class);
        startActivity(intent);
    }

    private void saveTreatmentPhoto() {

        hideSoftInput();

        focusHolder.requestFocus();

        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        // првоерка имени
        String nameToCheck = editTextPhotoDescription.getText().toString().trim();

        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutPhotoDescription.setError(getString(R.string.error_photo_description));
            editTextPhotoDescription.startAnimation(scaleAnimation);

            return;
        } else {
            textInputLayoutPhotoDescription.setError(null);
            textInputLayoutPhotoDescription.setErrorEnabled(false);
        }

        // проверка окончена, начинаем сохранение
        textPhotoDescription = nameToCheck;
        if (actionBar != null) {
            actionBar.setTitle(textPhotoDescription);
        }

        if (newTreatmentPhoto) {
            saveTreatmentPhotoToDataBase();
            newTreatmentPhoto = false;

            if (goBack) {
                goToTreatmentActivity();
            } else {
                editTreatmentPhoto = false;
                mDescriptionView.setVisibility(View.GONE);
                textDate.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

        } else {
            updateTreatmentPhotoToDataBase();

            if (goBack) {
                goToTreatmentActivity();
            } else {
                editTreatmentPhoto = false;
                mDescriptionView.setVisibility(View.GONE);
                textDate.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }
        }
    }

    private void saveTreatmentPhotoToDataBase() {
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTreatmentPhotoToDataBase() {
        //TODO реализовать обновление пользователя в базу
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto Updated To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteTreatmentPhotoFromDataBase() {
        //TODO реализовать удаление пользователя из базы
        Toast.makeText(this, "TreatmentPhoto Deleted from DataBase", Toast.LENGTH_LONG).show();
    }

    // мой зумм класс
    private class MyImageMatrixTouchHandler extends ImageMatrixTouchHandler {

        MyImageMatrixTouchHandler(Context context) {
            super(context);
        }

        MyImageMatrixTouchHandler(Context context, ImageMatrixCorrector corrector) {
            super(context, corrector);
        }

        // проверка в состоянии зума или нет
        final boolean[] inZoom = {false};

        // для DoubleTap
        boolean firstTouch = false;
        long time = 0;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            view.performClick();

            boolean result = super.onTouch(view, event);

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // првоерка DoubleTap (в промежутке 300 мс)
                // если DoubleTap, то не вызывается toggle()
                if (firstTouch && (System.currentTimeMillis() - time) <= 300) {
                    inZoom[0] = true;
                    firstTouch = false;
                } else {
                    firstTouch = true;
                    inZoom[0] = false;
                    time = System.currentTimeMillis();
                }

                // в отдельном потоке ожидаем 2 сек после ACTION_DOWN
                // в это время основной поток продолжает свою работу
                // если будут какие-то движения - они пойдут дальше
                // а, если не было никаких движений (увеличение, перемещение картинки)
                // то делаем  toggle()
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            if (!inZoom[0] && !landscape) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggle();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            // в остальных случаях не вызывается  toggle() блокировкой  inZoom[0] = true;
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN ||
                    event.getActionMasked() == MotionEvent.ACTION_POINTER_UP ||
                    event.getActionMasked() == MotionEvent.ACTION_MOVE) {

                inZoom[0] = true;
            }

            return result;
        }
    }

}




