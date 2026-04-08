package dev.dect.thumbs.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import dev.dect.thumbs.R;
import dev.dect.thumbs.adapter.ListButtonColorAdapter;
import dev.dect.thumbs.adapter.ListButtonSubTextAdapter;
import dev.dect.thumbs.adapter.ListButtonSwitchAdapter;
import dev.dect.thumbs.adapter.ListGroup;
import dev.dect.thumbs.adapter.ListSwitchAdapter;
import dev.dect.thumbs.data.AppSettings;
import dev.dect.thumbs.data.ThumbSettings;
import dev.dect.thumbs.generator.ThumbGenerator;
import dev.dect.thumbs.menu.MoreMenu;
import dev.dect.thumbs.model.ListButtonColorModel;
import dev.dect.thumbs.model.ListButtonSubTextModel;
import dev.dect.thumbs.model.ListButtonSwitchModel;
import dev.dect.thumbs.model.ListSwitchModel;
import dev.dect.thumbs.popup.AlignmentPopup;
import dev.dect.thumbs.popup.FullLoadingPopup;
import dev.dect.thumbs.popup.GridPopup;
import dev.dect.thumbs.popup.InputPopup;
import dev.dect.thumbs.popup.PickerFontPopup;
import dev.dect.thumbs.popup.ProgressPopup;
import dev.dect.thumbs.utils.AppFilesUtils;
import dev.dect.thumbs.utils.InterfaceUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AppSettings APP_SETTINGS;

    private ThumbSettings THUMB_SETTINGS;

    private ThumbGenerator PREVIEW_GENERATOR;

    private ImageView PREVIEW;

    private FullLoadingPopup LOADING_POPUP;

    private MoreMenu MORE_MENU;

    private ImageButton BTN_MORE;

    private final ActivityResultLauncher<String> PICK_MULTIPLE_VIDEOS_RESULT = registerForActivityResult(
        new ActivityResultContracts.GetMultipleContents(),
        this::filterFiles
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InterfaceUtils.StatusBar.updateColor(this);

        setContentView(R.layout.activity_main);

        initVariables();

        initListeners();

        init();
    }

    private void initVariables() {
        PREVIEW = findViewById(R.id.preview);

        BTN_MORE = findViewById(R.id.btnMore);

        MORE_MENU = new MoreMenu(BTN_MORE, () -> {
            APP_SETTINGS.setShowPreview(!APP_SETTINGS.isToShowPreview());

            if(APP_SETTINGS.isToShowPreview()) {
                Toast.makeText(MainActivity.this, R.string.toast_info_sample, Toast.LENGTH_SHORT).show();
            }

            setPreviewSetting();
        });

        LOADING_POPUP = new FullLoadingPopup(this);

        APP_SETTINGS = new AppSettings(this);

        THUMB_SETTINGS = new ThumbSettings(this);

        PREVIEW_GENERATOR = new ThumbGenerator(
            this,
                THUMB_SETTINGS,
            null,
            new ThumbGenerator.OnThumbGenerator() {
                @Override
                public void onSampleCompleted(Bitmap bitmap) {
                    PREVIEW.setImageBitmap(bitmap);

                    LOADING_POPUP.dismissByCode();
                }

                @Override
                public void onError(boolean fatal) {
                    if(fatal) {
                        Toast.makeText(MainActivity.this, R.string.toast_error_generic, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void initListeners() {
        BTN_MORE.setOnClickListener((v) -> MORE_MENU.show(APP_SETTINGS));

        findViewById(R.id.btnSelectGenerate).setOnClickListener((v) -> selectFiles());
    }

    private void init() {
        buildRecyclerView();

        setPreviewSetting();
    }

    private void setPreviewSetting() {
        final ConstraintLayout header = findViewById(R.id.header);

        final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) header.getLayoutParams();

        if(APP_SETTINGS.isToShowPreview()) {
            findViewById(R.id.titleCollapsed).setVisibility(View.GONE);

            layoutParams.height = 0;

            header.setLayoutParams(layoutParams);

            BTN_MORE.setBackgroundResource(R.drawable.btn_floating_background);

            PREVIEW.setVisibility(View.VISIBLE);

            generatePreview();
        } else {
            layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

            header.setLayoutParams(layoutParams);

            BTN_MORE.setBackgroundColor(getColor(R.color.activity_background));

            findViewById(R.id.titleCollapsed).setVisibility(View.VISIBLE);

            PREVIEW.setVisibility(View.GONE);
        }
    }

    private void buildRecyclerView() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        concatAdapter.addAdapter(getImageGroupAdapter());
        concatAdapter.addAdapter(getThumbnailGroupAdapter());
        concatAdapter.addAdapter(getTimestampGroupAdapter());
        concatAdapter.addAdapter(getTitleGroupAdapter());
        concatAdapter.addAdapter(getGeneralGroupAdapter());
        concatAdapter.addAdapter(getPerformanceGroupAdapter());
        concatAdapter.addAdapter(getOutputGroupAdapter());

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(concatAdapter);
    }

    /** @noinspection CodeBlock2Expr*/
    private ListGroup.Adapter getImageGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListButtonColorModel> listButtonColor = new ArrayList<>();

        listButtonColor.add(
            new ListButtonColorModel(
                R.string.setting_image_background,
                THUMB_SETTINGS.getBackgroundColor(),
                (color) -> {
                    THUMB_SETTINGS.setBackgroundColor(color);

                    generatePreview();
                },
                false
            )
        );

        concatAdapter.addAdapter(new ListButtonColorAdapter(listButtonColor));

        final ArrayList<ListButtonSubTextModel> listButtonSubtext = new ArrayList<>();

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_image_margin,
                THUMB_SETTINGS.getMarginInPx() + " px",
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_image_margin,
                        THUMB_SETTINGS.getMarginInPx(),
                        0,
                        200,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setMarginInPx(input);

                                listButtonSubTextModel.setValue(input + " px");

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_image_grid,
                THUMB_SETTINGS.getRowNumber() + "x" + THUMB_SETTINGS.getColumnNumber(),
                false,
                (listButtonSubTextModel, pos) -> {
                    new GridPopup(
                        MainActivity.this,
                        R.string.setting_image_grid,
                        THUMB_SETTINGS.getRowNumber(),
                        THUMB_SETTINGS.getColumnNumber(),
                        R.string.popup_btn_set,
                        (rows, columns) -> {
                            THUMB_SETTINGS.setRowNumber(rows);
                            THUMB_SETTINGS.setColumnNumber(columns);

                            listButtonSubTextModel.setValue(THUMB_SETTINGS.getRowNumber() + "x" + THUMB_SETTINGS.getColumnNumber());

                            concatAdapter.notifyItemChanged(pos);

                            generatePreview();
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false
                    ).show();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListButtonSubTextAdapter(listButtonSubtext));

        return new ListGroup.Adapter(new ListGroup(ListGroup.NO_TITLE, concatAdapter));
    }

    /** @noinspection CodeBlock2Expr*/
    private ListGroup.Adapter getThumbnailGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListButtonSubTextModel> listButtonSubtext = new ArrayList<>();

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_thumbnail_max_size,
                THUMB_SETTINGS.getThumbnailMaxSize() + " px",
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_thumbnail_max_size,
                        THUMB_SETTINGS.getThumbnailMaxSize(),
                        1,
                        1440,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setThumbnailMaxSize(input);

                                listButtonSubTextModel.setValue(input + " px");

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_thumbnail_space,
                THUMB_SETTINGS.getSpaceBetweenThumbnailsInPx() + " px",
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_thumbnail_space,
                        THUMB_SETTINGS.getSpaceBetweenThumbnailsInPx(),
                        0,
                        200,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setSpaceBetweenThumbnailsInPx(input);

                                listButtonSubTextModel.setValue(input + " px");

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        concatAdapter.addAdapter(new ListButtonSubTextAdapter(listButtonSubtext));

        final ArrayList<ListButtonColorModel> listButtonColor = new ArrayList<>();

        listButtonColor.add(
            new ListButtonColorModel(
                R.string.setting_thumbnail_shadow,
                THUMB_SETTINGS.getThumbnailShadowColor(),
                true,
                (color) -> {
                    THUMB_SETTINGS.setThumbnailShadowColor(color);

                    generatePreview();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListButtonColorAdapter(listButtonColor));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_thumbnail, concatAdapter));
    }

    /** @noinspection CodeBlock2Expr*/
    private ListGroup.Adapter getTimestampGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListButtonSubTextModel> listButtonSubtext = new ArrayList<>();

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_timestamp_position,
                THUMB_SETTINGS.getTimestampPositionName(MainActivity.this),
                false,
                (listButtonSubTextModel, pos) -> {
                    new AlignmentPopup(
                        MainActivity.this,
                        THUMB_SETTINGS.getTimestampVerticalPosition(),
                        THUMB_SETTINGS.getTimestampHorizontalPosition(),
                        (v, h) -> {
                            THUMB_SETTINGS.setTimestampVerticalPosition(v);
                            THUMB_SETTINGS.setTimestampHorizontalPosition(h);

                            listButtonSubTextModel.setValue(THUMB_SETTINGS.getTimestampPositionName(MainActivity.this));

                            concatAdapter.notifyItemChanged(pos);

                            generatePreview();
                        }
                    ).show();
                },
                false
            )
        );

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_timestamp_size,
                String.valueOf(THUMB_SETTINGS.getTimestampSize()),
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_timestamp_size,
                        THUMB_SETTINGS.getTimestampSize(),
                        1,
                        150,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setTimestampSize(input);

                                listButtonSubTextModel.setValue(input);

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_timestamp_margin,
                THUMB_SETTINGS.getTimestampMarginInPx() + " px",
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_timestamp_margin,
                        THUMB_SETTINGS.getTimestampMarginInPx(),
                        1,
                        100,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setTimestampMarginInPx(input);

                                listButtonSubTextModel.setValue(input + " px");

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        concatAdapter.addAdapter(new ListButtonSubTextAdapter(listButtonSubtext));

        final ArrayList<ListButtonColorModel> listButtonColor = new ArrayList<>();

        listButtonColor.add(
            new ListButtonColorModel(
                R.string.setting_timestamp_text_color,
                THUMB_SETTINGS.getTimestampTextColor(),
                true,
                (color) -> {
                    THUMB_SETTINGS.setTimestampTextColor(color);

                    generatePreview();
                },
                false
            )
        );

        listButtonColor.add(
            new ListButtonColorModel(
                R.string.setting_timestamp_shadow_color,
                THUMB_SETTINGS.getTimestampTextShadowColor(),
                true,
                (color) -> {
                    THUMB_SETTINGS.setTimestampTextShadowColor(color);

                    generatePreview();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListButtonColorAdapter(listButtonColor));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_timestamp, concatAdapter));
    }

    /** @noinspection CodeBlock2Expr*/
    private ListGroup.Adapter getTitleGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListButtonSwitchModel> listButtonSwitch = new ArrayList<>();

        listButtonSwitch.add(
            new ListButtonSwitchModel(
                R.string.setting_title_name,
                THUMB_SETTINGS.getNameText(),
                new ListButtonSwitchModel.OnListButtonSwitchModel() {
                    @Override
                    public void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos) {
                        new InputPopup.Text(
                            MainActivity.this,
                            R.string.setting_title_name,
                            THUMB_SETTINGS.getNameText(),
                            R.string.popup_btn_set,
                            new InputPopup.OnInputPopup() {
                                @Override
                                public void onStringInputSet(String input) {
                                    THUMB_SETTINGS.setNameText(input);

                                    listButtonSubText.setValue(input);

                                    concatAdapter.notifyItemChanged(pos);

                                    generatePreview();
                                }
                            },
                            R.string.popup_btn_cancel,
                            null,
                            true,
                            false,
                            false
                        ).show();
                    }

                    @Override
                    public void onChange(boolean b) {
                        THUMB_SETTINGS.setShowNameInTitle(b);

                        generatePreview();
                    }
                },
                THUMB_SETTINGS.isToShowNameInTitle(),
                false
            )
        );

        listButtonSwitch.add(
            new ListButtonSwitchModel(
                R.string.setting_title_resolution,
                THUMB_SETTINGS.getResolutionText(),
                new ListButtonSwitchModel.OnListButtonSwitchModel() {
                    @Override
                    public void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos) {
                        new InputPopup.Text(
                            MainActivity.this,
                            R.string.setting_title_resolution,
                            THUMB_SETTINGS.getResolutionText(),
                            R.string.popup_btn_set,
                            new InputPopup.OnInputPopup() {
                                @Override
                                public void onStringInputSet(String input) {
                                    THUMB_SETTINGS.setResolutionText(input);

                                    listButtonSubText.setValue(input);

                                    concatAdapter.notifyItemChanged(pos);

                                    generatePreview();
                                }
                            },
                            R.string.popup_btn_cancel,
                            null,
                            true,
                            false,
                                false
                        ).show();
                    }

                    @Override
                    public void onChange(boolean b) {
                        THUMB_SETTINGS.setShowResolutionInTitle(b);

                        generatePreview();
                    }
                },
                THUMB_SETTINGS.isToShowResolutionInTitle(),
                false
            )
        );

        listButtonSwitch.add(
            new ListButtonSwitchModel(
                R.string.setting_title_frames,
                THUMB_SETTINGS.getFramesText(),
                new ListButtonSwitchModel.OnListButtonSwitchModel() {
                    @Override
                    public void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos) {
                        new InputPopup.Text(
                            MainActivity.this,
                            R.string.setting_title_frames,
                            THUMB_SETTINGS.getFramesText(),
                            R.string.popup_btn_set,
                            new InputPopup.OnInputPopup() {
                                @Override
                                public void onStringInputSet(String input) {
                                    THUMB_SETTINGS.setFramesText(input);

                                    listButtonSubText.setValue(input);

                                    concatAdapter.notifyItemChanged(pos);

                                    generatePreview();
                                }
                            },
                            R.string.popup_btn_cancel,
                            null,
                            true,
                            false,
                            false
                        ).show();
                    }

                    @Override
                    public void onChange(boolean b) {
                        THUMB_SETTINGS.setShowFramesInTitle(b);

                        generatePreview();
                    }
                },
                THUMB_SETTINGS.isToShowFramesInTitle(),
                false
            )
        );

        listButtonSwitch.add(
            new ListButtonSwitchModel(
                R.string.setting_title_duration,
                THUMB_SETTINGS.getDurationText(),
                new ListButtonSwitchModel.OnListButtonSwitchModel() {
                    @Override
                    public void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos) {
                        new InputPopup.Text(
                            MainActivity.this,
                            R.string.setting_title_duration,
                            THUMB_SETTINGS.getDurationText(),
                            R.string.popup_btn_set,
                            new InputPopup.OnInputPopup() {
                                @Override
                                public void onStringInputSet(String input) {
                                    THUMB_SETTINGS.setDurationText(input);

                                    listButtonSubText.setValue(input);

                                    concatAdapter.notifyItemChanged(pos);

                                    generatePreview();
                                }
                            },
                            R.string.popup_btn_cancel,
                            null,
                            true,
                            false,
                                false
                        ).show();
                    }

                    @Override
                    public void onChange(boolean b) {
                        THUMB_SETTINGS.setShowDurationInTitle(b);

                        generatePreview();
                    }
                },
                THUMB_SETTINGS.isToShowDurationInTitle(),
                false
            )
        );

        listButtonSwitch.add(
            new ListButtonSwitchModel(
                R.string.setting_title_size,
                THUMB_SETTINGS.getSizeText(),
                new ListButtonSwitchModel.OnListButtonSwitchModel() {
                    @Override
                    public void onButtonClicked(ListButtonSwitchModel listButtonSubText, int pos) {
                        new InputPopup.Text(
                            MainActivity.this,
                            R.string.setting_title_size,
                            THUMB_SETTINGS.getSizeText(),
                            R.string.popup_btn_set,
                            new InputPopup.OnInputPopup() {
                                @Override
                                public void onStringInputSet(String input) {
                                    THUMB_SETTINGS.setSizeText(input);

                                    listButtonSubText.setValue(input);

                                    concatAdapter.notifyItemChanged(pos);

                                    generatePreview();
                                }
                            },
                            R.string.popup_btn_cancel,
                            null,
                            true,
                            false,
                            false
                        ).show();
                    }

                    @Override
                    public void onChange(boolean b) {
                        THUMB_SETTINGS.setShowSizeInTitle(b);

                        generatePreview();
                    }
                },
                THUMB_SETTINGS.isToShowSizeInTitle(),
                false
            )
        );

        concatAdapter.addAdapter(new ListButtonSwitchAdapter(listButtonSwitch));

        final ArrayList<ListButtonColorModel> listButtonColor = new ArrayList<>();

        listButtonColor.add(
            new ListButtonColorModel(
                R.string.setting_title_title_color,
                THUMB_SETTINGS.getTitleColor(),
                (color) -> {
                    THUMB_SETTINGS.setTitleColor(color);

                    generatePreview();
                },
                false
            )
        );

        concatAdapter.addAdapter(new ListButtonColorAdapter(listButtonColor));

        final ArrayList<ListButtonSubTextModel> listButtonSubtext = new ArrayList<>();

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_title_title_size,
                String.valueOf(THUMB_SETTINGS.getTitleSize()),
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_title_title_size,
                        THUMB_SETTINGS.getTitleSize(),
                        1,
                        150,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setTitleSize(input);

                                listButtonSubTextModel.setValue(input);

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                false
            )
        );

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_title_title_space,
                THUMB_SETTINGS.getTitleSpaceBetweenLinesInPx() + " px",
                false,
                (listButtonSubTextModel, pos) -> {
                    new InputPopup.NumberInteger(
                        MainActivity.this,
                        R.string.setting_title_title_space,
                        THUMB_SETTINGS.getTitleSpaceBetweenLinesInPx(),
                        1,
                        100,
                        R.string.popup_btn_set,
                        new InputPopup.OnInputPopup() {
                            @Override
                            public void onIntInputSet(int input) {
                                THUMB_SETTINGS.setTitleSpaceBetweenLinesInPx(input);

                                listButtonSubTextModel.setValue(input + " px");

                                concatAdapter.notifyItemChanged(pos);

                                generatePreview();
                            }
                        },
                        R.string.popup_btn_cancel,
                        null,
                        true,
                        false,
                        false
                    ).show();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListButtonSubTextAdapter(listButtonSubtext));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_title, concatAdapter));
    }

    /** @noinspection CodeBlock2Expr*/
    private ListGroup.Adapter getGeneralGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListButtonSubTextModel> listButtonSubtext = new ArrayList<>();

        listButtonSubtext.add(
            new ListButtonSubTextModel(
                R.string.setting_general_font,
                THUMB_SETTINGS.getFont().getName(),
                (listButtonSubTextModel, pos) -> {
                    new PickerFontPopup(
                        MainActivity.this,
                        THUMB_SETTINGS.getFont(),
                        (font) -> {
                            THUMB_SETTINGS.setFont(font);

                            listButtonSubTextModel.setValue(font.getName());

                            concatAdapter.notifyItemChanged(pos);

                            generatePreview();
                        }
                    ).show();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListButtonSubTextAdapter(listButtonSubtext));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_general, concatAdapter));
    }

    private ListGroup.Adapter getOutputGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListSwitchModel> listSwitch = new ArrayList<>();

        listSwitch.add(
            new ListSwitchModel(
                R.string.setting_output_file,
                THUMB_SETTINGS.isToUseFileNameToSave() ? R.string.setting_output_file_eg_file : R.string.setting_output_file_eg_uuid,
                THUMB_SETTINGS.isToUseFileNameToSave(),
                (listSwitchModel, b, pos) -> {
                    THUMB_SETTINGS.setUseFileNameToSave(b);

                    listSwitchModel.setIdSubTitle(b ? R.string.setting_output_file_eg_file : R.string.setting_output_file_eg_uuid);

                    concatAdapter.notifyItemChanged(pos);
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListSwitchAdapter(listSwitch));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_output, concatAdapter));
    }

    private ListGroup.Adapter getPerformanceGroupAdapter() {
        final ConcatAdapter concatAdapter = new ConcatAdapter();

        final ArrayList<ListSwitchModel> listSwitch = new ArrayList<>();

        listSwitch.add(
            new ListSwitchModel(
                R.string.setting_performance_speed,
                R.string.setting_performance_speed_message,
                THUMB_SETTINGS.isToUseHighPrecision(),
                (listSwitchModel, b, pos) -> {
                    THUMB_SETTINGS.setUseHighPrecision(b);

                    generatePreview();
                },
                true
            )
        );

        concatAdapter.addAdapter(new ListSwitchAdapter(listSwitch));

        return new ListGroup.Adapter(new ListGroup(R.string.setting_performance, concatAdapter));
    }

    private void generatePreview() {
        if(APP_SETTINGS.isToShowPreview()) {
            LOADING_POPUP.show();

            PREVIEW.setImageDrawable(null);

            Executors.newSingleThreadExecutor().execute(() -> PREVIEW_GENERATOR.generate());
        }
    }

    private void selectFiles() {
        PICK_MULTIPLE_VIDEOS_RESULT.launch("*/*");
    }

    @SuppressLint("Range")
    private void filterFiles(List<Uri> selectedUris) {
        if(selectedUris.isEmpty()) {
            return;
        }

        final ArrayList<Uri> videoUris = new ArrayList<>();

        for(Uri uri : selectedUris) {
            final Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if(cursor != null) {
                cursor.moveToFirst();

                try {
                    final String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)),
                                 extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(),
                                 type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                    if(type != null && type.contains("video")) {
                        videoUris.add(uri);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "filterFiles: " + e.getMessage());
                }

                cursor.close();
            }
        }

        if(videoUris.isEmpty()) {
            Toast.makeText(this, R.string.toast_error_wrong_type, Toast.LENGTH_SHORT).show();
        } else {
            generate(videoUris);
        }
    }

    private void generate(List<Uri> uris) {
        final AtomicInteger progress = new AtomicInteger(0),
                            completed = new AtomicInteger(0);

        final ProgressPopup progressPopup = new ProgressPopup(this, R.string.popup_generating);

        progressPopup.setMax(THUMB_SETTINGS.getColumnNumber() * THUMB_SETTINGS.getRowNumber() * uris.size());

        progressPopup.setValue(0);

        progressPopup.show();

        for(Uri uri : uris) {
            Executors.newSingleThreadExecutor().execute(() -> {
                new ThumbGenerator(
                    this,
                    THUMB_SETTINGS,
                    uri,
                    new ThumbGenerator.OnThumbGenerator() {
                        @Override
                        public void onProgress() {
                            progressPopup.setValue(progress.incrementAndGet());
                        }

                        @Override
                        public void onCompleted() {
                            if(completed.incrementAndGet() == uris.size()) {
                                progressPopup.dismissWithAnimation();

                                showSuccessSnackBar(completed.get());
                            }
                        }

                        @Override
                        public void onError(boolean fatal) {
                            Toast.makeText(MainActivity.this, R.string.toast_error_generic, Toast.LENGTH_SHORT).show();
                        }
                    }
                ).generate();
            });
        }
    }

    private void showSuccessSnackBar(int amount) {
        final File folder = AppFilesUtils.getAppFolder(this);

        String text = folder.getAbsolutePath().replaceFirst("/storage/emulated/0/", "");

        if(amount == 1) {
            text = getString(R.string.snack_saved_at_singular).replaceFirst("%s", text);
        } else {
            text = getString(R.string.snack_saved_at_plural)
                .replaceFirst("%d", String.valueOf(amount))
                .replaceFirst("%s", text);
        }

        Snackbar
        .make(
            findViewById(R.id.content),
            text,
            Snackbar.LENGTH_SHORT
        ).setBackgroundTint(getColor(R.color.snack_background))
        .setTextColor(getColor(R.color.snack_text))
        .setActionTextColor(getColor(R.color.snack_text_action))
        .setAction(
            R.string.snack_btn_show,
            (v) -> {
                try {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setDataAndType(
                        FileProvider.getUriForFile(this, getPackageName(), folder),
                    "resource/folder"
                    );

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "showSuccessSnackBar - 1: " + e.getMessage());

                    try {
                        final Intent intent2 = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent2);
                    } catch (Exception e2) {
                        Log.e(TAG, "showSuccessSnackBar - 2: " + e2.getMessage());

                        Toast.makeText(this, R.string.toast_error_open_folder, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        ).show();
    }
}