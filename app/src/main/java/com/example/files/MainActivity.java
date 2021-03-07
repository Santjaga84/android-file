package com.example.files;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReadFromFile;
    private Button btnSaveToFile;
    private Button btnShowFile;

    //private Button btnSaveFile;

    private EditText txtContentToSave;
    private TextView txtFileContent;
    private TextView txtFileList;

    private RadioButton rbOverwrite;
    private RadioButton rbAppend;

    private EditText txtFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {


        btnReadFromFile = findViewById(R.id.btnReadFromFile);
        btnSaveToFile = findViewById(R.id.btnSaveToFile);
        btnShowFile = findViewById(R.id.btnShowFile);

        //btnSaveFile = findViewById(R.id.btnSaveFile);

        txtFile =findViewById(R.id.txtFile);

        btnReadFromFile.setOnClickListener(this);
        btnSaveToFile.setOnClickListener(this);
        btnShowFile.setOnClickListener(this);

        //btnSaveFile.setOnClickListener(this);

        rbOverwrite = findViewById(R.id.rbOverwrite);
        rbAppend = findViewById(R.id.rbAppend);
        //Текст для вставки из файла
        txtFileContent = findViewById(R.id.txtFileContent);

        //Текст для записи в файл
        txtContentToSave = findViewById(R.id.txtContentToSave);

        //Для вывода списка файлов в папке /file
        txtFileList = findViewById(R.id.txtFileList);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnReadFromFile:
                readTextFromFile();
                break;

            case R.id.btnSaveToFile:
                saveTextToFile();
                break;
            case R.id.btnShowFile:
                showFilesInFilesFolder();
                 break;


        }
    }


    private void showFilesInFilesFolder() {
    //Метод, который возвращает все файлы из папки /files
        String[] fileInFileFolder = fileList();
        String listOfFileAsString = "";

        for (int i = 0; i < fileInFileFolder.length; i++){
            listOfFileAsString += fileInFileFolder[i] + "\n";
        }
        txtFileList.setText(listOfFileAsString);
    }


    private void saveTextToFile() {

        //Исходящий поток для записи в файлы
        //После использования потоки нужно ОБЯЗАТЕЛЬНО ЗАКРЫВАТЬ!!!
        FileOutputStream fos = null;

        //делаем попытку записать файл
        try {

            //Открыть файл для записи.Метод возвращает поток готовый для записи в конкретный файл
            //Mode_PRIVATE - файлы доступны только для техх кто их создал.Файл будет перезаписываться каждый раз
            //MODE_APPEND - включается для того чтобы ДОПИСАТЬ данные в существующий файл
            //TODO: дать пользователю возможность выбирать режим работы записи в файл


                File file = new File(getFilesDir(), String.valueOf(txtFile));
                 file = file.createNewFile();
                fos = openFileOutput(String.valueOf(file.createNewFile()), setWriteMode());
               // fos = openFileOutput(Constans.txtFileName, setWriteMode());

            //Получаем текст и конвертируем его в двоичный буфер
            byte[] bufferToWrite = (txtContentToSave.getText().toString() + System.lineSeparator()).getBytes();

            //Пишем в файловый поток массив из байт
            fos.write(bufferToWrite);

            Toast.makeText(this, "Files saved", Toast.LENGTH_LONG).show();

        }
        //Перехватываем исключительные ситуации доступа к файловой системе
        //Ошибки ввода-вывода
        catch (IOException ex)
        {
            Toast.makeText(this, "IO Error" + ex.getMessage(),Toast.LENGTH_LONG).show();
        }

        //Блок finally исполняется при любом раскладе и при успешной отработке блока try
        //и при возникновении exception
        finally {

            //При попытке закрыть поток он может быть null по разным причинам
            //а значит его тоже нужно ПОПЫТАТЬСЯ закрыть
            try {

                fos.close();
            }catch (IOException e){
               Toast.makeText(this,"Files Stream Closing Error" + e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private int setWriteMode() {

        int writeMode = MODE_PRIVATE;

        if (rbAppend.isChecked())
            writeMode = MODE_APPEND;

        return writeMode;
    }

    private void readTextFromFile() {

        FileInputStream fin = null;

        try {

                fin = openFileInput(Constans.txtFileName);
              //  fin = openFileInput(String.valueOf(R.id.txtFile));


            //Нужно подготовить буфер для чтения данных из файла
            //Для установки точной величины буфера воспользуемся Stream.available()
            //Этот метод возвращает количество байт ожидающих чтения в потоке
            //Этот подход безопасен  лишь для малых объемов данных
            //Для больших файлов  создаются буферы фиксированной величины и файлы качаются
            //порциями через буфер

            byte[] bytesOfFile = new byte[fin.available()];

            //читаем данные из файла в указанный буффер в ОЗУ(оперативной памяти)
            fin.read(bytesOfFile);


            //Создаем текст из массива байт в буффере , используя спец. возможности конструктора String
            String textFromFile = new String(bytesOfFile);

            txtFileContent.setText(textFromFile);

        } catch (FileNotFoundException ex){

            Toast.makeText(this,"IO Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

            catch (IOException ex){

            Toast.makeText(this,"Input FileStream Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
    }
}