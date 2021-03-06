package com.example.delia.onlinedormselect.dormitory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.delia.onlinedormselect.R;
import com.example.delia.onlinedormselect.bean.RoomInfo;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class FourModeActivity extends Activity implements View.OnClickListener
{

    private static final int UPDATE_ROOM_INFO = 1;
    private static final int CHECK_GENDER=2;//新加的

    private SharedPreferences sharedPreferences;

    private ImageView backBtn;

    private String name, id ,gender, gender_1, gender_2, gender_3;

    private String id1, veriCode1, id2, veriCode2, id3, veriCode3;

    private TextView mName, mId, mGender;

    private TextView mBuilding1Name , mBuilding2Name, mBuilding3Name, mBuilding4Name, mBuilding5Name;

    private TextView mBuilding1,mBuilding2,mBuilding3,mBuilding4,mBuilding5;

    private int building;

    private Spinner mSpinner;

    private List<String> data_list;

    private ArrayAdapter<String> arr_adapter;

    private EditText editText1_id, editText1_veriCode, editText2_id , editText2_veriCode, editText3_id , editText3_veriCode;

    private TextView mVerify;

    private int errcode = 1;
    private int err_code = 1;

    private RoomInfo roomInfo = null;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_ROOM_INFO:
                    updateRoomInfo((RoomInfo) msg.obj );
                    break;
                case CHECK_GENDER://新加的
                    checkGender((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.four_mode_layout);

        sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);

        roomInfo = new RoomInfo();

        data_list = new ArrayList<>();

        initView();
    }

    public void initView()
    {
        backBtn = (ImageView)findViewById(R.id.four_back_login);
        backBtn.setOnClickListener(this);

        mBuilding1Name = (TextView)findViewById(R.id.four_building1_name);
        mBuilding2Name = (TextView)findViewById(R.id.four_building2_name);
        mBuilding3Name = (TextView)findViewById(R.id.four_building3_name);
        mBuilding4Name = (TextView)findViewById(R.id.four_building4_name);
        mBuilding5Name = (TextView)findViewById(R.id.four_building5_name);

        mBuilding1 = (TextView)findViewById(R.id.four_building1);
        mBuilding2 = (TextView)findViewById(R.id.four_building2);
        mBuilding3 = (TextView)findViewById(R.id.four_building3);
        mBuilding4 = (TextView)findViewById(R.id.four_building4);
        mBuilding5 = (TextView)findViewById(R.id.four_building5);

        mName = (TextView)findViewById(R.id.four_name);
        mId = (TextView)findViewById(R.id.four_id);
        mGender = (TextView)findViewById(R.id.four_gender);

        editText1_id = (EditText)findViewById(R.id.four_mate1_id);
        editText1_veriCode = (EditText)findViewById(R.id.four_mate1_veriCode);
        editText2_id = (EditText)findViewById(R.id.four_mate2_id);
        editText2_veriCode = (EditText)findViewById(R.id.four_mate2_veriCode);
        editText3_id = (EditText)findViewById(R.id.four_mate3_id);
        editText3_veriCode = (EditText)findViewById(R.id.four_mate3_veriCode);

        name = sharedPreferences.getString("studentName" , "保密");
        id = sharedPreferences.getString("studentId" , "0000000000");
        gender = sharedPreferences.getString("studentGender" , "男");

        mName.setText(name);
        mId.setText(id);
        mGender.setText(gender);

        String genderCode = "0";
        if(gender == "男")
        {
            genderCode = "0";
        }
        else
        {
            genderCode = "1";
        }

        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/getRoom?gender="+genderCode;
        queryInternet(address);

        mSpinner = (Spinner)findViewById(R.id.four_spinner);

        //数据

        if(roomInfo.getFive() != null && !roomInfo.getFive().equals("0"))
        {
            data_list.add("5号楼");
        }
        if(roomInfo.getThirteen() != null && !roomInfo.getThirteen().equals("0"))
        {
            data_list.add("13号楼");
        }
        if(roomInfo.getFourteen() != null && !roomInfo.getFourteen().equals("0"))
        {
            data_list.add("14号楼");
        }
        if(roomInfo.getEight() != null && !roomInfo.getEight().equals("0"))
        {
            data_list.add("8号楼");
        }
        if(roomInfo.getNine() != null && !roomInfo.getNine().equals("0"))
        {
            data_list.add("9号楼");
        }

        //适配器
        arr_adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        mSpinner.setAdapter(arr_adapter);

        //mate1
        editText1_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                id1 = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length()==10)
                {
                    String str="https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid="+editText1_id.getText();
                    queryInternetforGender(str,1);
                }
            }
        });

        editText1_veriCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                veriCode1 = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });
        //mate2
        editText2_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                id2 = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length()==10)
                {
                    String str="https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid="+editText1_id.getText();
                    queryInternetforGender(str,2);
                }
            }
        });

        editText2_veriCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                veriCode2 = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        //mate3
        editText3_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                id3 = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length()==10)
                {
                    String str="https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid="+editText1_id.getText();
                    queryInternetforGender(str,3);
                }
            }
        });

        editText3_veriCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                veriCode3 = charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        mVerify = (TextView)findViewById(R.id.four_verify);
        mVerify.setOnClickListener(this);

    }

    public void queryInternet(final String address)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                //API接口
                HttpURLConnection con = null;
                try
                {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());

                    URL url = new URL(address);
                    con = (HttpsURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);

                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String str;
                    while ( (str = reader.readLine()) != null )
                    {
                        response.append(str);
                    }
                    String jsonData = response.toString();

                    Log.d("dormSelect" , jsonData);
                    //调用解析方法
                    parseJSON(jsonData);

                    reader.close();
                    in.close();

                    if(errcode == 0 && roomInfo.getFive() != null)
                    {

                        Log.d("dormSelect" , "test");
                        //子线程与主线程的通信机制
                        Message msg = new Message();
                        msg.what = UPDATE_ROOM_INFO;
                        msg.obj = roomInfo;
                        mHandler.sendMessage(msg);
                    }

                }
                catch (Exception e)
                {
                    Log.d("dormSelect", "error");
                    e.printStackTrace();
                }
                finally
                {
                    if (con != null)
                    {
                        con.disconnect();
                    }

                }

            }


        }).start();//将该线程加入资源等待队列

    }

    public void queryInternetByPost(final String address)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                //API接口
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");

                    //数据准备
                    int num = 1;
                    //String data = "num="+num+"&stuid="+id+"&stu1id="+"&v1code="+"&stu2id="+"&v2code="+"&stu3id="+"&v3code="+"&buildingNo="+building;
                    String data = "{\n" +
                            "        \"num\":" + num + ",\n" +
                            "        \"stuid\":" + id + ",\n" +
                            "        \"stu1id\":" + id1 + ",\n" +
                            "        \"v1code\":" + veriCode1 +",\n" +
                            "        \"stu2id\":" + id2 +",\n" +
                            "        \"v2code\":" + veriCode2 +",\n" +
                            "        \"stu3id\":" + id3 +",\n" +
                            "        \"v3code\":" + veriCode3 +",\n" +
                            "        \"buildingNo\":" + building + "\n" +
                            "    }\n" +
                            "}";

                    //至少要设置的两个请求头
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length()+"");
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());

                    int responseCode = connection.getResponseCode();
                    Log.d("dormSelect" , String.valueOf(responseCode));

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String str;
                    while ( (str = reader.readLine()) != null )
                    {
                        response.append(str);
                    }
                    String jsonData = response.toString();

                    Log.d("dormSelect" , jsonData);
                    //调用解析方法
                    parseJSONByPost(jsonData);

                    if(err_code == 0)
                    {
                        Intent intent = new Intent(FourModeActivity.this , DormSuccessActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(FourModeActivity.this , DormFailedActivity.class);
                        startActivity(intent);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e)
                {
                    Log.d("dormSelect", "error");
                    e.printStackTrace();
                }
                finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }

            }
        }).start();//将该线程加入资源等待队列

    }



    private void parseJSON(String jsonData)
    {
        try
        {
            JSONTokener jsonParser = new JSONTokener(jsonData);
            JSONObject loginfos = (JSONObject) jsonParser.nextValue();
            if(loginfos.has("errcode")==true)
            {
                errcode=Integer.parseInt(loginfos.getString("errcode"));
            }
            if(loginfos.has("error_code")==true)
            {
                err_code = Integer.parseInt(loginfos.getString("error_code"));

            }
            if(loginfos.has("data"))
            {
                String datas=loginfos.getString("data");
                JSONTokener jsonParser_data = new JSONTokener(datas);
                JSONObject room_data = (JSONObject) jsonParser_data.nextValue();

                if(room_data.has("5")==true)
                {
                    roomInfo.setFive(room_data.getString("5"));
                    roomInfo.setThirteen(room_data.getString("13"));
                    roomInfo.setFourteen(room_data.getString("14"));
                    roomInfo.setEight((room_data.getString("8")));
                    roomInfo.setNine(room_data.getString("9"));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void parseJSONByPost(String jsonData)
    {
        try
        {
            JSONTokener jsonParser = new JSONTokener(jsonData);
            JSONObject loginfos = (JSONObject) jsonParser.nextValue();
            if(loginfos.has("errcode")==true)
            {
                err_code = Integer.parseInt(loginfos.getString("errcode"));

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void updateRoomInfo(RoomInfo roomInfo)
    {
        mBuilding1Name.setText("5号楼");
        mBuilding1.setText(roomInfo.getFive());
        mBuilding2Name.setText("13号楼");
        mBuilding2.setText(roomInfo.getThirteen());
        mBuilding3Name.setText("14号楼");
        mBuilding3.setText(roomInfo.getFourteen());
        mBuilding4Name.setText("8号楼");
        mBuilding4.setText(roomInfo.getEight());
        mBuilding5Name.setText("9号楼");
        mBuilding5.setText(roomInfo.getNine());

        if(roomInfo.getFive() != null && !roomInfo.getFive().equals("0"))
        {
            data_list.add("5号楼");
        }
        if(roomInfo.getThirteen() != null && !roomInfo.getThirteen().equals("0"))
        {
            data_list.add("13号楼");
        }
        if(roomInfo.getFourteen() != null && !roomInfo.getFourteen().equals("0"))
        {
            data_list.add("14号楼");
        }
        if(roomInfo.getEight() != null && !roomInfo.getEight().equals("0"))
        {
            data_list.add("8号楼");
        }
        if(roomInfo.getNine() != null && !roomInfo.getNine().equals("0"))
        {
            data_list.add("9号楼");
        }

        //适配器
        arr_adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        mSpinner.setAdapter(arr_adapter);

    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.four_verify )
        {
            if(id1!=null && veriCode1!=null && id2!=null && veriCode2!=null &id3!=null && veriCode3!=null)
            {
                building  = Integer.parseInt(String.valueOf(mSpinner.getSelectedItem()).replace("号楼",""));

                final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/SelectRoom";

                queryInternetByPost(address);
            }
            else
            {
                Toast.makeText(FourModeActivity.this , "同住人信息不能为空" , Toast.LENGTH_LONG).show();
            }
        }
        if(view.getId() == R.id.four_back_login)
        {
            Intent i = new Intent();

            finish();
        }
    }

    public void queryInternetforGender(final String address, final int mateNum)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                //API接口
                HttpURLConnection con = null;
                try
                {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());

                    URL url = new URL(address);
                    con = (HttpsURLConnection) url.openConnection();

                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);

                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String str;
                    while ( (str = reader.readLine()) != null )
                    {
                        response.append(str);
                    }
                    String jsonData = response.toString();

                    Log.d("dormSelect" , jsonData);
                    //调用解析方法
                    if(mateNum==1)
                    {
                        gender_1=parseJSONforGender(jsonData);
                    }
                    if(mateNum==2)
                    {
                        gender_2=parseJSONforGender(jsonData);
                    }
                    if(mateNum==3)
                    {
                        gender_3=parseJSONforGender(jsonData);
                    }


                    reader.close();
                    in.close();
                    Message msg = new Message();
                    msg.what = CHECK_GENDER;
                    if(mateNum==1)
                    {
                        msg.obj = gender_1;
                    }
                    if(mateNum==2)
                    {
                        msg.obj = gender_2;
                    }

                    if(mateNum==3)
                    {
                        msg.obj = gender_3;
                    }
                    mHandler.sendMessage(msg);

                }
                catch (Exception e)
                {
                    Log.d("dormSelect", "error");
                    e.printStackTrace();
                }
                finally
                {
                    if (con != null)
                    {
                        con.disconnect();
                    }

                }

            }


        }).start();//将该线程加入资源等待队列

    }

    private String parseJSONforGender(String jsonData)
    {
        String gender_json="";
        try
        {
            JSONTokener jsonParser = new JSONTokener(jsonData);
            JSONObject loginfos = (JSONObject) jsonParser.nextValue();
            if(loginfos.has("data"))
            {
                String datas=loginfos.getString("data");
                JSONTokener jsonParser_data = new JSONTokener(datas);
                JSONObject stu_data = (JSONObject) jsonParser_data.nextValue();

                if(stu_data.has("gender")==true)
                {
                    gender_json=stu_data.getString("gender");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return gender_json;
    }

    public void checkGender(String str)
    {
        if(!str.equals(gender))
        {
            Toast.makeText(FourModeActivity.this,"性别不符或学号不存在", Toast.LENGTH_LONG).show();
            editText1_id.setText("");
            editText1_id.setHint("请输入合法学号");
            mVerify.setEnabled(false);
        }
        else {
            mVerify.setEnabled(true);
        }
    }
}
