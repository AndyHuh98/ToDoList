package csce4623.andrewhe.todolist.addedittodoitem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import csce4623.andrewhe.todolist.R;
import csce4623.andrewhe.todolist.data.ToDoItem;

public class AddEditToDoItemActivity extends AppCompatActivity {

    private Integer previousRequestCode;
    private TextView mTextView;
    private EditText etTitle;
    private EditText etContent;
    private EditText etSetDate;
    private Button btnSaveItem;
    private Button btnDeleteItem;
    private CheckBox chkbxCompleted;

    private ToDoItem item;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(("MM-dd-yyyy HH : mm"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do_item);

        mTextView = (TextView) findViewById(R.id.text);
        btnSaveItem = findViewById(R.id.btnSaveToDoItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        etContent = findViewById(R.id.etItemContent);
        etTitle = findViewById(R.id.etItemTitle);
        etSetDate = findViewById(R.id.etSetDate);
        chkbxCompleted = findViewById(R.id.chkbxCompleted);

        Intent callingIntent = getIntent();
        if (callingIntent.getIntExtra("requestCode", 0) == 1) {
            Log.d("AddEditToDoItemActivity", "Update Request");
            item = (ToDoItem) callingIntent.getSerializableExtra("ToDoItem");
            Log.d("AddEditToDoItemActivity", item.getCompleted().toString());
        } else {
            Log.d("AddEditToDoItemActivity", "Create Request");
            item = new ToDoItem();
        }

        previousRequestCode = callingIntent.getIntExtra("requestCode", 0);

        Log.d("AddEditToDoItemActivity", "completed: " + item.getCompleted());

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        etSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etSetDate);
            }
        });

        chkbxCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setCompleted(chkbxCompleted.isChecked());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        etTitle.setText(item.getTitle());
        etContent.setText(item.getContent());

        if (item.getDueDate() != 0) {
            String dateString = dateFormat.format(new Date(item.getDueDate()));
            etSetDate.setText(dateString);
        }

        chkbxCompleted.setChecked(item.getCompleted());
    }

    @Override
    protected void onStop() {
        super.onStop();
        item.setTitle(etTitle.getText().toString());
        item.setContent(etContent.getText().toString());
        item.setDueDate(getLongDateFromString(etSetDate.getText().toString()));
        chkbxCompleted.setChecked(item.getCompleted());
    }

    private long getLongDateFromString(String dateTimeString) {
        Log.d("AddEditToDoItemActivity", dateTimeString);
        if (!dateTimeString.isEmpty()) {
            try {
                Date date = dateFormat.parse(dateTimeString);
                long dateLong = date.getTime();
                Log.d("AddEditToDoItemActivity", String.valueOf(dateLong));
                return dateLong;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void updateItem() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        long dateLong = getLongDateFromString(etSetDate.getText().toString());

        if (!(title.isEmpty() && content.isEmpty() && dateLong == 0)) {
            item.setTitle(title);
            item.setContent(content);
            item.setDueDate(dateLong);

            Intent dataIntent = new Intent();
            dataIntent.putExtra("ToDoItem", item);
            setResult(RESULT_OK, dataIntent);
        }
        finish();
    }

    private void deleteItem() {
        if (previousRequestCode == 1) {
            Intent dataIntent = new Intent();
            dataIntent.putExtra("ToDoItem", item);
            dataIntent.putExtra("deleteFlag", true);
            setResult(RESULT_OK, dataIntent);
        }
        finish();
    }

    private void showDatePicker(final EditText etSetDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                etSetDate.setText((month + 1) + "-" + day + "-" + year);
                showTimePicker(etSetDate);
            }
        }, year, month, day);

        datePickerDialog.setTitle("Select Due Date");
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText etSetDate) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                etSetDate.append((" " + hour + " : " + minute));
            }
        }, hour, minute, true);

        timePickerDialog.setTitle("Select Due Date Time");
        timePickerDialog.show();
    }
}