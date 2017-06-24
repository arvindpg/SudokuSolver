package com.example.pg.opencv;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pg.opencv.R;

public class Main2Activity extends AppCompatActivity {
    int array[][][]=new int[9][9][10];
    EditText e[][]=new EditText[9][9];
    Button compute;
    Button reset1;
    int n;
    String TAG="MainActivity";
    Handler mHandler;
    Handler mHandler1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(TAG, "onCreate:");
        mHandler=new Handler();
        mHandler1=new Handler();

        Toast.makeText(getApplicationContext(),"Default Sudoku Problem",Toast.LENGTH_SHORT).show();




        for(int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++)
            {
                for(int k=0;k<10;k++)
                    array[i][j][k]=k;
            }
        }
        for(int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++)
            {
                String editID="b"+i+j;
                int resID=getResources().getIdentifier(editID,"id",getPackageName());
                e[i][j] =((EditText)findViewById(resID));

            }
        }
        input();

        mHandler.post(mUpdate);

        Toast.makeText(getApplicationContext(),"You can change the problem",Toast.LENGTH_SHORT).show();
        compute=(Button)findViewById(R.id.comp);
           compute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Computing",Toast.LENGTH_SHORT).show();

                    n = 0;
                    do {
                        eliminate();
                        eliminaterc();
                        eliminatebox();
                        putit();
                        putitline();
                        putitbox();
                        mHandler.post(mUpdate);

                        n++;
                    } while (n <110);




                Toast.makeText(getApplicationContext(),"Computing FInished",Toast.LENGTH_SHORT).show();


            }


        });
        reset1=(Button)findViewById(R.id.reset);
        reset1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i=0;i<9;i++)
                {
                    for(int j=0;j<9;j++){
                        e[i][j].setText("");
                        for(int k=0;k<9;k++){
                            array[i][j][k]=k;
                        }
                    }
                }
            }
        });





    };
    private Runnable mUpdate = new Runnable() {
        public void run() {

            display();
            mHandler.postDelayed(this, 1000);

        }
    };
    private Runnable mUpdate1 = new Runnable() {
        public void run() {


                display();

                mHandler.postDelayed(this, 10000);









        }
    };
    void display(){
        for(int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++){
                if(array[i][j][0]!=0)
                    e[i][j].setText(String.valueOf(array[i][j][0]));
            }
        }}
    boolean notfinish()
    { int i,j;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(array[i][j][0]==0)
                    return  true;
            }
        }
        return false;
    }


    void input()
    {
        Log.d(TAG, "input: ");

        e[0][3].setText("7");
        e[1][0].setText("1");
        e[0][3].setText("4");
        e[0][3].setText("3");
        e[0][3].setText("2");
        e[0][3].setText("6");
        e[0][3].setText("9");
        e[0][3].setText("5");
        e[0][3].setText("8");
        e[0][3].setText("4");
        e[0][3].setText("1");

       /* for(int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++){
                String number=e[i][j].getText().toString();
                if(!number.matches(""))
                {
                    array[i][j][0]=Integer.parseInt(number);
                }


            }
//        }*/
        array[0][3][0]=7;
        array[1][0][0]=1;
        array[2][3][0]=4;
        array[2][4][0]=3;
        array[2][6][0]=2;
        array[3][8][0]=6;
        array[4][5][0]=9;
        array[4][3][0]=5;
        array[5][8][0]=8;
        array[5][6][0]=4;
        array[5][7][0]=1;
        array[6][5][0]=1;
        array[6][4][0]=8;
        array[7][7][0]=5;
        array[7][2][0]=2;
        array[8][6][0]=3;
        array[8][1][0]=4;


    }


    void eliminate()
    {
        int i,j,k;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(array[i][j][0]!=0)
                {
                    for(k=1;k<10;k++)
                        array[i][j][k]=0;

                }
            }
        }
    }
    void eliminaterc()
    {
        int i,j,k,r,c;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(array[i][j][0]!=0)
                {
                    k=array[i][j][0];
                    for(c=0;c<9;c++)
                    {
                        array[i][c][k]=0;
                    }

                    for(r=0;r<9;r++)
                    {
                        array[r][j][k]=0;
                    }

                }
            }
        }
    }
    void eliminatebox()
    {
        int i,j,k,r,c;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {
                if(array[i][j][0]!=0)
                {   k=array[i][j][0];
                    for(r=(i/3)*3;r<(i/3)*3+3;r++)
                    {
                        for(c=(j/3)*3;c<(j/3)*3+3;c++)
                        {
                            array[r][c][k]=0;
                        }
                    }
                }
            }
        }
    }
    void putit()
    {
        int i,j,k,n=0,count=0;
        for(i=0;i<9;i++)
        {
            for(j=0;j<9;j++)
            {count=0;
                for(k=1;k<10;k++)
                {
                    if(array[i][j][k]!=0)
                    {
                        count++;
                        n=array[i][j][k];
                    }}
                if(count==1)
                {
                    array[i][j][0]=n;
                    mHandler.post(mUpdate);

                }

            }
        }
    }
    void putitline()
    {
        int i,j,k,n,count,r=0,c=0;
        for(n=1;n<10;n++)
        {
            for(i=0;i<9;i++)
            {  count =0;
                for(j=0;j<9;j++)
                {
                    if(array[i][j][n]==n)
                    {   r=i;
                        c=j;
                        count++;
                    }
                }
                if(count==1) {
                    array[r][c][0] = n;
                    e[r][c].setText(String.valueOf(n));
                }
            }

            for(i=0;i<9;i++)
            {  count =0;
                for(j=0;j<9;j++)
                {
                    if(array[j][i][n]==n)
                    {r=i;
                        c=j;
                        count++;
                    }
                }
                if(count==1) {
                    array[c][r][0] = n;
                    mHandler.post(mUpdate);
                   // e[c][r].setText(String.valueOf(n));
                }
            }
        }
    }
    void putitbox()
    { int n,i,j,k,l,m,o,q=0,w=0,count=0,z;



        for(i=0;i<3;i++)
        {
            for(j=0;j<3;j++)
            {
                for(n=1;n<10;n++)
                { count=0;

                    for(k=0;k<3;k++)
                    {
                        for(l=0;l<3;l++)
                        {
                            m=i*3;
                            o=j*3;
                            if(array[m+k][o+l][n]==n)
                            {

                                q=m+k;
                                w=o+l;
                                count++;
                            }
                        }
                    }
                    if(count==1)
                    {
                        array[q][w][0]=n;
                        mHandler.post(mUpdate);
                        //e[q][w].setText(String.valueOf(n));
                    }
                }
            }
        }
    }
}

