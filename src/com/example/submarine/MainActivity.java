package com.example.submarine;

import java.util.Date;
import java.util.Random;
import com.example.submarine.Members.Award;
import com.example.submarine.Members.Button;
import com.example.submarine.Members.Ship;
import com.example.submarine.Members.Submarine;
import com.example.submarine.Members.Bottom;
import com.example.submarine.Parameter.Bottomid;
import com.example.submarine.Parameter.Difficulty;
import com.example.submarine.Parameter.Mode;
import com.example.submarine.Parameter.Type;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

	Parameter parameter=new Parameter();//参数库
	Members members=new Members();//类库
	private Submarine me=members.new Submarine(10,380,0);//己方潜艇
	private int contentviewID=0;//ContentView标识
	private boolean MessageboxShowing=false;//check中消息框是否正在显示
	private Button Pause_btn=members.new Button();//暂停按钮
	private boolean pause=false;//是否暂停
	private Button Quit_btn=members.new Button();//退出按钮
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		contentviewID=1;//主界面ID为1
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保持屏幕常亮
	} 

	public void newgame()
	{
		Parameter.allenemies.clear();
		Parameter.alltorpedos.clear();
		Parameter.allverticaltorpedos.clear();
		Parameter.allmissiles.clear();
		Parameter.allships.clear();
		Parameter.allbombs.clear();
		Parameter.allawards.clear();
		for(int i=0;i<=9;i++)
			Parameter.alllabels.get(i).reset();
		Parameter.enemies=0;
		Parameter.ships=0;
		Parameter.score=0;
		Parameter.lives=5;
//		t_alert=false;
//		b_alert=false;
		me.setx(10);
		me.sety((int)(380*Parameter.heightRatio));
		me.setblood(100);
		Parameter.mytorpedos=0;
		Parameter.level=1;
		
		MessageboxShowing=false;//关闭消息框显示状态
	}
	
	/**图片转灰度**/
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();    
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
	
	class MyView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{
		private Paint paint;
		private SurfaceHolder mHolder;
		private int counttime=80;//敌军生成时间
		private Random rd = new Random(new Date().getTime());//随机数
		private int awardtime=0;//奖励生成时间
		//private boolean detecttorpedo=false;//是否侦测到鱼雷
		//private boolean detectbomb=false;//是否侦测到炸弹
		private boolean swell=true;//波浪是否鼓起
		private Paint backgroundPaint=new Paint();//渐变背景画笔
		private int bottomtime=0;//底部生成时间
		private int bottomid=0;//底部类型
		
		/**控制游戏循环**/
		boolean IsRunning = false;

		/**SensorManager管理器**/
		private SensorManager mSensorMgr = null;    
		Sensor mSensor = null; 

		/**重力感应X轴 Y轴 Z轴的重力值**/
		private float mGX = 0;
		private float mGY = 0;
		private float mGZ = 0;
		
		/**每40帧刷新一次屏幕**/  
		public static final long TIME_IN_FRAME = 40;
		
		public MyView(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			paint = new Paint(); //创建Paint 
			paint.setAntiAlias(true); //设置抗锯齿效果 

			/**得到SensorManager对象**/
			mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);   
			mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   
			mSensorMgr.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);// 注册listener
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			IsRunning=true;//设置运行状态
			/**获取屏幕宽高**/
			Parameter.screenWidth = this.getWidth();
			Parameter.screenHeight = this.getHeight();
			/**获取屏幕缩放比例**/
			Parameter.widthRatio=(float)Parameter.screenWidth/1280;
			Parameter.heightRatio=(float)Parameter.screenHeight/720;
			Parameter.barHeight=(int)(60*Parameter.heightRatio);//设置状态栏高度
			/**设定渐变背景**/
			backgroundPaint.setStyle(Style.FILL_AND_STROKE);
			LinearGradient lg=new LinearGradient(0,0,0,Parameter.screenHeight,Color.parseColor("#88F7FF"),Color.parseColor("#2649ED"),Shader.TileMode.MIRROR);
			backgroundPaint.setShader(lg);
			new Thread(new MyThread()).start();	//开始新线程
		}

		void prepare()
		{
			Parameter.MySubmarineImg=BitmapFactory.decodeResource(getResources(), R.drawable.mysubmarine);
			Parameter.EnemySubmarine1Img=BitmapFactory.decodeResource(getResources(), R.drawable.enemysubmarine);
			Parameter.EnemySubmarine2Img=BitmapFactory.decodeResource(getResources(), R.drawable.enemysubmarine2);
			Parameter.MyTorpedoImg=BitmapFactory.decodeResource(getResources(), R.drawable.mytorpedo);
			Parameter.EnemyTorpedoImg=BitmapFactory.decodeResource(getResources(), R.drawable.enemytorpedo);
			Parameter.VerticalTorpedoImg=BitmapFactory.decodeResource(getResources(), R.drawable.verticaltorpedo);
			Parameter.ExplosionImg=BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
			Parameter.Coral1Img=BitmapFactory.decodeResource(getResources(), R.drawable.coral1);
			Parameter.Coral2Img=BitmapFactory.decodeResource(getResources(), R.drawable.coral2);
			Parameter.Coral3Img=BitmapFactory.decodeResource(getResources(), R.drawable.coral3);
			Parameter.Rock1Img=BitmapFactory.decodeResource(getResources(), R.drawable.rock1);
			Parameter.Rock2Img=BitmapFactory.decodeResource(getResources(), R.drawable.rock2);
			Parameter.Grass1Img=BitmapFactory.decodeResource(getResources(), R.drawable.grass1);
			Parameter.Grass2Img=BitmapFactory.decodeResource(getResources(), R.drawable.grass2);
			Parameter.Grass3Img=BitmapFactory.decodeResource(getResources(), R.drawable.grass3);
			Parameter.ShipImg=BitmapFactory.decodeResource(getResources(), R.drawable.ship);
			Parameter.BombImg=BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
			Matrix matrix = new Matrix();
			matrix.postRotate(-45);//旋转图片
			Parameter.ShipImg_Rotated = Bitmap.createBitmap(Parameter.ShipImg, 0, 0, Parameter.ShipImg.getWidth(), Parameter.ShipImg.getHeight(),matrix, true);//船下沉图片
			
			for(int i=1;i<=10;i++)
				Parameter.alllabels.add(members.new Label(i));
			
			/**暂停按钮属性**/
			Pause_btn.text = "Pause";
			Pause_btn.x=(int)(940*Parameter.widthRatio);
			Pause_btn.y=(int)(5*Parameter.heightRatio);
			Pause_btn.size_x=(int)(150*Parameter.widthRatio);
			Pause_btn.size_y=(int)(50*Parameter.heightRatio);
			Pause_btn.text_x=(int)(955*Parameter.widthRatio);
			Pause_btn.text_y=(int)(45*Parameter.heightRatio);
			Pause_btn.color_normal=Color.rgb(18,50,180);
			Pause_btn.color_on=Color.rgb(10,26,90);
			Pause_btn.color_text=Color.WHITE;
			
			/**退出按钮属性**/
			Quit_btn.text="Quit";
			Quit_btn.x=(int)(1110*Parameter.widthRatio);
			Quit_btn.y=(int)(5*Parameter.heightRatio);
			Quit_btn.size_x=(int)(110*Parameter.widthRatio);
			Quit_btn.size_y=(int)(50*Parameter.heightRatio);
			Quit_btn.text_x=(int)(1125*Parameter.widthRatio);
			Quit_btn.text_y=(int)(45*Parameter.heightRatio);
			Quit_btn.color_normal=Color.rgb(18,50,180);
			Quit_btn.color_on=Color.rgb(10,26,90);
			Quit_btn.color_text=Color.WHITE;
		}
		
		void produceenemy()
		{
			++counttime;
			if(counttime==100)
			{
				if(Parameter.enemies<Parameter.maxenemies)
				{
					int x=(int)((950+rd.nextInt(200))*Parameter.widthRatio);
					int y=(int)((250+rd.nextInt(400))*Parameter.heightRatio);
					Submarine e=members.new Submarine(x,y,parameter.ENEMY);
					Parameter.allenemies.add(e);
					Parameter.enemies++;
				}
				if(Parameter.ships<Parameter.maxships)
				{
					int x=(int)((20+rd.nextInt(600))*Parameter.widthRatio);
					Ship s=members.new Ship(x);
					Parameter.allships.addElement(s);
					Parameter.ships++;
				}
				counttime=0;
			}
		}

		void produceaward()
		{
			awardtime++;
			if(awardtime==800)
			{
				awardtime=0;
				int y=(int)((rd.nextInt(500)+200)*Parameter.heightRatio);
				int choice=rd.nextInt(12);
				Type type=Type.None;
				switch(choice)
				{
				case 1:case 2:case 3:type=Type.Blood;break;
				case 4:case 5:case 6:type=Type.NewMissile;break;
				case 7:case 8:type=Type.Live;break;
				case 9:type=Type.Allclear;break;
				case 10:case 11:type=Type.Score;break;
				}
				if(type!=Type.None)
				{
					Award a=members.new Award(y,type);
					Parameter.allawards.add(a);
				}
			}
		}

		void setmissile()
		{
			int x=0;
			int y=0;
			boolean got=false;
			for(int i=0;i<Parameter.allenemies.size();i++)
			{
				if(Parameter.allenemies.get(i).getexist())
				{
					x=Parameter.allenemies.get(i).getx();
					y=Parameter.allenemies.get(i).gety();
					got=true;
					break;
				}
			}
			for(int i=0;i<Parameter.allmissiles.size();i++)
			{
				if(got)
				{
					double angle=Math.atan((double)(y-Parameter.allmissiles.get(i).gety())/(x-Parameter.allmissiles.get(i).getx()));
					Parameter.allmissiles.get(i).setangle(angle);
				}
				else
					Parameter.allmissiles.get(i).setangle(0);
			}
		}

		/**绘制状态栏**/
		void text(Canvas canvas)
		{
			paint.setStyle(Style.FILL_AND_STROKE);//绘制轮廓并填充
			paint.setColor(Color.rgb(33,100,220));//设定状态栏颜色
			Rect bar_rect=new Rect(0, 0, Parameter.screenWidth, Parameter.barHeight);//设定状态栏尺寸
			canvas.drawRect(bar_rect, paint);//绘制矩形条
			paint.setColor(Color.WHITE);//设定字体颜色
			paint.setTextSize(40*Parameter.heightRatio);//设定字体大小
			canvas.drawText("Score:"+Parameter.score, 20*Parameter.widthRatio, 45*Parameter.heightRatio, paint);//分数
			canvas.drawText("Level:"+Parameter.level, 300*Parameter.widthRatio, 45*Parameter.heightRatio, paint);//关卡
			
			/**导弹准备进度条**/
			if(Parameter.missiletime<500)
				Parameter.missiletime++;
			RectF missile_rectF=new RectF(488*Parameter.widthRatio,8*Parameter.heightRatio,692*Parameter.widthRatio,52*Parameter.heightRatio);
			RectF missile_rectF_inner=new RectF(490*Parameter.widthRatio,10*Parameter.heightRatio,(490+Parameter.missiletime/5*2)*Parameter.widthRatio,50*Parameter.heightRatio);
			paint.setStyle(Style.STROKE);//仅绘制轮廓
			paint.setColor(Color.BLACK);//设置轮廓颜色
			canvas.drawRect(missile_rectF, paint);//绘制进度条外轮廓
			paint.setStyle(Style.FILL_AND_STROKE);//绘制轮廓并填充
			if(Parameter.missiletime>=250)
				paint.setColor(Color.rgb((int)(-1.02*Parameter.missiletime+510),255,0)); 
			else
				paint.setColor(Color.rgb(255,(int)(1.02*Parameter.missiletime),0));
			canvas.drawRect(missile_rectF_inner, paint);
		
			paint.setColor(Color.WHITE);//设定字体颜色
			canvas.drawText("Lives:"+Parameter.lives, 740*Parameter.widthRatio, 45*Parameter.heightRatio, paint);//生命
			
			Pause_btn.draw(canvas);//暂停按钮绘制
			Quit_btn.draw(canvas);//退出按钮绘制
		}
		
		void computer_ship(Ship s)
		{
			int choice=0;
			int computertime=s.getcomputertime();
			s.setcomputertime(++computertime);
			if(s.getcomputertime()==60)
			{
				s.setcomputertime(0);
				if(Parameter.difficulty!=Difficulty.Hard)
				{
					choice=rd.nextInt(5);
					if(s.getexploded())
						return;
					switch(choice)
					{
					case 0:case 1:s.setdirection(parameter.LEFT);;break;
					case 2:case 3:s.setdirection(parameter.RIGHT);break;
					case 4:s.launch();break;
					}
				}
				else
				{
					if(s.getx()>me.getx()+50)
						s.setdirection(parameter.LEFT);
					else if(s.getx()<me.getx()-50)
						s.setdirection(parameter.RIGHT);
					else
						s.launch();
				}
			}
			s.move();
		}
		
		void computer_submarine(Submarine s)
		{
			int choice=0;
			int computertime=s.getcomputertime();
			s.setcomputertime(++computertime);
			if(s.getcomputertime()==30)
			{
				s.setcomputertime(0);
				if(Parameter.difficulty!=Difficulty.Hard)
				{
					choice=1+rd.nextInt(5);
					if(s.getexploded())
						return;
					switch(choice)
					{
					case 1:s.setdirectionY(parameter.UP);break;
					case 2:s.setdirectionY(parameter.DOWN);break;
					case 3:
						if(s.getx()>800*Parameter.widthRatio) 
							s.setdirectionX(parameter.LEFT); 
						else
							s.launch(s.getmeposition());break;
					case 4:s.setdirectionX(parameter.RIGHT);break;
					case 5:s.launch(s.getmeposition());break;
					}
				}
				else
				{
					if(s.gety()>me.gety()+50)
						s.setdirectionY(parameter.UP);
					else if(s.gety()<me.gety()-50)
						s.setdirectionY(parameter.DOWN);
					else
						s.launch(s.getmeposition());
				}
			}
			s.move();
		}
		
		void wave(Canvas canvas)
		{
			float y=0;
			if(Parameter.flex>=15)
				swell=false;
			else if(Parameter.flex<=1)
				swell=true;
			if(Parameter.wavedisplacement==628)//628=200*PI
				Parameter.wavedisplacement=0;
			paint.setColor(Color.rgb(185, 255, 255));
			Path path=new Path();
			path.moveTo(0, Parameter.flex*(float)Math.sin(0.01*(float)Parameter.wavedisplacement)+100);
			for(float i=(float)Parameter.wavedisplacement;i<=Parameter.wavedisplacement+Parameter.screenWidth;i+=1)
			{
				y=Parameter.flex*(float)Math.sin(0.01*(float)i)+150*Parameter.heightRatio;
				path.lineTo(i-Parameter.wavedisplacement, y);
				canvas.drawPoint(i-Parameter.wavedisplacement, y, paint);
			}
			if(swell) 
				Parameter.flex+=0.1f;
			else 
				Parameter.flex-=0.1f;
			Parameter.wavedisplacement+=4;
			path.lineTo(Parameter.screenWidth, Parameter.screenHeight);
			path.lineTo(0, Parameter.screenHeight);
			path.close();
			canvas.drawPath(path, backgroundPaint);
		}
		
		void producebottom()
		{
			++bottomtime;
			if(bottomtime==40)
			{
				bottomtime=0;
				bottomid=rd.nextInt(40);
				Bottom b=members.new Bottom(Bottomid.None);
				switch(bottomid)
				{
				case 1:case 1+20:b.setid(Bottomid.Grass1);Parameter.allbottom.add(b);break;
				case 2:case 2+20:b.setid(Bottomid.Grass2);Parameter.allbottom.add(b);break;
				case 3:case 3+20:b.setid(Bottomid.Grass3);Parameter.allbottom.add(b);break;
				case 4:b.setid(Bottomid.Rock1);Parameter.allbottom.add(b);break;
				case 5:b.setid(Bottomid.Rock2);Parameter.allbottom.add(b);break;
				case 6:b.setid(Bottomid.Coral1);Parameter.allbottom.add(b);break;
				case 7:b.setid(Bottomid.Coral2);Parameter.allbottom.add(b);break;
				case 8:b.setid(Bottomid.Coral3);Parameter.allbottom.add(b);break;
				}
			}
		}
		
		public void draw(Canvas canvas)
		{
			text(canvas);
			RectF backgroundRectF=new RectF(0, Parameter.barHeight, Parameter.screenWidth, Parameter.screenHeight);
			paint.setStyle(Style.FILL_AND_STROKE);//绘制轮廓并填充
			paint.setColor(Color.rgb(185, 255, 255));
			canvas.drawRect(backgroundRectF, paint);
			for(int i=0;i<Parameter.allships.size();i++)
			{
				if(Parameter.allships.get(i).getexist())
				{
					computer_ship(Parameter.allships.get(i));
					Parameter.allships.get(i).draw(canvas);
				}
			}
			wave(canvas);
			me.move();
			me.draw(canvas);
			for(int i=0;i<Parameter.allenemies.size();i++)
			{
				if(Parameter.allenemies.get(i).getexist())
				{
					computer_submarine(Parameter.allenemies.get(i));
					Parameter.allenemies.get(i).draw(canvas);
				}
			}
			for(int i=0;i<Parameter.alltorpedos.size();i++)
			{
				if(Parameter.alltorpedos.get(i).getexist())
				{
					Parameter.alltorpedos.get(i).move();
					Parameter.alltorpedos.get(i).draw(canvas);
				}
			}
			for(int i=0;i<Parameter.allverticaltorpedos.size();i++)
			{
				if(Parameter.allverticaltorpedos.get(i).getexist())
				{
					Parameter.allverticaltorpedos.get(i).move();
					Parameter.allverticaltorpedos.get(i).draw(canvas);
				}
			}
			for(int i=0;i<Parameter.allmissiles.size();i++)
			{
				if(Parameter.allmissiles.get(i).getexist())
				{
					Parameter.allmissiles.get(i).move();
					Parameter.allmissiles.get(i).draw(canvas);
				}
			}
			for(int i=0;i<Parameter.allbottom.size();i++)
			{
				if(Parameter.allbottom.get(i).getexist())
				{
					Parameter.allbottom.get(i).move();
					Parameter.allbottom.get(i).draw(canvas);
				}
			}
			producebottom();
			for(int i=0;i<Parameter.allbombs.size();i++)
			{
				if(Parameter.allbombs.get(i).getexist())
				{
					Parameter.allbombs.get(i).move();
					Parameter.allbombs.get(i).draw(canvas);
				}
			}
			for(int i=0;i<Parameter.allawards.size();i++)
			{
				if(Parameter.allawards.get(i).getexist())
				{
					Parameter.allawards.get(i).move();
					Parameter.allawards.get(i).draw(canvas);
				}
			}
			//alert();
			if(Parameter.newlevel && Parameter.mode==Mode.Level)
				for(int i=0;i<Parameter.alllabels.size();i++)
				{
					if(Parameter.alllabels.get(i).getexist())
					{
						Parameter.alllabels.get(i).move();
						Parameter.alllabels.get(i).draw(canvas);
						break;
					}
				}
		}
		
		void begin()
		{
			runOnUiThread(new Runnable()    
	        {    
	            public void run()    
	            {
	            	setContentView(R.layout.activity_main);//返回开始界面
	            	contentviewID=1;//更改ID
	            }
	        });
			try {
				Thread.sleep(500);//休眠以加载开始界面
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		void check()
		{
			int mx=me.getx();
			int my=me.gety();
			int ex=0;
			int tx=0;
			int ey=0;
			int ty=0;
			int sx=0;
			int sy=0;
			int bx=0;
			int by=0;
			int ax=0;
			int ay=0;
			int missilex=0;
			int missiley=0;
			int enemyblood=0;
			int shipblood=0;
			int myblood=me.getblood();
			
			/**检测奖励**/
			for(int i=0;i<Parameter.allawards.size();i++)
			{
				ax=Parameter.allawards.get(i).getx();
				ay=Parameter.allawards.get(i).gety();
				if(Parameter.allawards.get(i).getexist() && mx>ax-100 && mx<ax+13 && my>ay-43 && my<ay+43)
				{
					Parameter.allawards.get(i).setexist(false);
					switch(Parameter.allawards.get(i).gettype())
					{
					case Blood:me.setblood(100);break;
					case Live:Parameter.lives++;break;
					case NewMissile:Parameter.missiletime=500;break;
					case Allclear:
						for(int j=0;j<Parameter.allenemies.size();j++)
							if(Parameter.allenemies.get(j).getexist())
								Parameter.allenemies.get(j).setblood(0);
						for(int j=0;j<Parameter.allships.size();j++)
							if(Parameter.allships.get(j).getexist())
								Parameter.allships.get(j).setblood(0);
						break;
					case Score:
						Parameter.score+=50+Parameter.difficulty.ordinal()*50;
						break;
					default:
						break;
					}
				}
			}
			
			/**检测潜艇爆炸**/
			for(int i=0;i<Parameter.allenemies.size();i++)
			{
				ex=Parameter.allenemies.get(i).getx();
				ey=Parameter.allenemies.get(i).gety();
				enemyblood=Parameter.allenemies.get(i).getblood();
				myblood=me.getblood();
				//detecttorpedo=false;
				if(mx<ex+90)
					Parameter.allenemies.get(i).setmeposition(parameter.LEFT);
				else 
					Parameter.allenemies.get(i).setmeposition(parameter.RIGHT);
				for(int j=0;j<Parameter.alltorpedos.size();j++)
				{
					if(Parameter.alltorpedos.get(j).getexist() && !Parameter.allenemies.get(i).getexploded())
					{
						tx=Parameter.alltorpedos.get(j).getx();
						ty=Parameter.alltorpedos.get(j).gety();
						if(Parameter.alltorpedos.get(j).getidentity()==parameter.MYSELF)
						{
							if(ex>tx-38 && ex<=tx+38 && ey>=ty-40 && ey<=ty+10)
							{
								Parameter.allenemies.get(i).setblood(enemyblood-Parameter.mytorpedopower);
								Parameter.alltorpedos.get(j).setexist(false);
								--Parameter.mytorpedos;
							}
						}
						else if(Parameter.alltorpedos.get(j).getidentity()==parameter.ENEMY)
							if(Parameter.allenemies.get(i).getexist() && my>=ty-40 && my<=ty+10 && mx<=tx+38)
							{
								//detecttorpedo=true;
								if(!Parameter.alltorpedos.get(j).getalarmplayed())
								{
									//t_alarm_played=false;
									Parameter.alltorpedos.get(j).setalarmplayed(true);
								}
								if(mx>tx-48)
								{
									me.setblood(myblood-Parameter.enemytorpedopower);
									Parameter.alltorpedos.get(j).setexist(false);
								}
							}
					}
				}
//				if(detecttorpedo) 
//					t_alert=true;
//				else
//					t_alert=false;
				for(int j=0;j<Parameter.allmissiles.size();j++)
				{
					if(Parameter.allmissiles.get(j).getexist() && !Parameter.allenemies.get(i).getexploded())
					{
						missilex=Parameter.allmissiles.get(j).getx();
						missiley=Parameter.allmissiles.get(j).gety();
						if(ex>missilex-58 && ex<=missilex+58 && ey>=missiley-40 && ey<=missiley+10)
						{
							Parameter.allmissiles.get(j).setexist(false);
							Parameter.allenemies.get(i).setblood(0);
							Parameter.missilesent=false;
						}
					}
				}
				if(mx>ex-87 && mx<ex+87 && my>ey-30 && my<ey+30)
				{
					if(!Parameter.allenemies.get(i).gethit() && !Parameter.allenemies.get(i).getexploded())
					{
						Parameter.allenemies.get(i).sethit(true);
						me.setblood(myblood-20);
						Parameter.allenemies.get(i).setblood(0);
					}
				}
				else
					Parameter.allenemies.get(i).sethit(false);
				if(!Parameter.allenemies.get(i).getexploded() && enemyblood<20)
				{
					Parameter.score+=10+Parameter.difficulty.ordinal()*10;
					--Parameter.enemies;
					Parameter.allenemies.get(i).setexploded(true);
					//if(Parameter.Sound==ON)
						//PlaySound(L"D:\\Submarine\\EXPLODE.WAV",NULL,SND_FILENAME | SND_ASYNC);
					if(Parameter.mode==Mode.Level)
					{
						int templevel=Parameter.level;
						Parameter.level=(int)((-25+Math.sqrt(625+100*(double)Parameter.score))/50+1);
						if(Parameter.level-templevel>=1)
							Parameter.newlevel=true;
					}
				}
			}
			
			/**检测船只爆炸**/
			for(int i=0;i<Parameter.allships.size();i++)
			{
				sx=Parameter.allships.get(i).getx();
				sy=Parameter.allships.get(i).gety();
				shipblood=Parameter.allships.get(i).getblood();
				for(int j=0;j<Parameter.allverticaltorpedos.size();j++)
					if(Parameter.allverticaltorpedos.get(j).getexist() && !Parameter.allships.get(i).getexploded())
					{
						tx=Parameter.allverticaltorpedos.get(j).getx();
						ty=Parameter.allverticaltorpedos.get(j).gety();
						if(sx>tx-100 && sx<=tx+82 && sy>=ty-80 && sy<=ty+30)
						{
							Parameter.allships.get(i).setblood(shipblood-(int)(Parameter.mytorpedopower*0.67));
							Parameter.allverticaltorpedos.get(j).setexist(false);
							--Parameter.mytorpedos;
						}
					}
					if(!Parameter.allships.get(i).getexploded() && shipblood<20)
					{
						Parameter.score+=30+Parameter.difficulty.ordinal()*30;
						--Parameter.ships;
						Parameter.allships.get(i).setexploded(true);
						//if(Sound==ON)
							//PlaySound(L"D:\\Submarine\\EXPLODE.WAV",NULL,SND_FILENAME | SND_ASYNC);
						if(Parameter.mode==Mode.Level)
						{
							int templevel=Parameter.level;
							Parameter.level=(int)((-25+Math.sqrt(625+100*(double)Parameter.score))/50+1);
							if(Parameter.level-templevel>=1)
								Parameter.newlevel=true;
						}
					}
			}
			
			/**判断是否通关**/
			if(Parameter.mode==Mode.Level && Parameter.level>=10)
			{
				MessageboxShowing=true;
				runOnUiThread(new Runnable()    
				{    
					public void run()    
					{
						new  AlertDialog.Builder(getContext())
						.setTitle("Congratulations" )
						.setMessage("You are win!" )
						.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								begin();
								newgame();
								return;
							}})
							.show();
					}
				});
			}
			//detectbomb=false;
			
			/**判断炸弹是否击中**/
			for(int i=0;i<Parameter.allbombs.size();i++)
			{
				myblood=me.getblood();
				bx=Parameter.allbombs.get(i).getx();
				by=Parameter.allbombs.get(i).gety();
				if(!Parameter.allbombs.get(i).getalarmplayed())
				{
					//b_alarm_played=false;
					Parameter.allbombs.get(i).setalarmplayed(true);
				}
				if(Parameter.allbombs.get(i).getexist() && mx>bx-120 && mx<=bx+32 && my>=by-20)
				{
					//detectbomb=true;
					if(my<=by+20)
					{
						me.setblood(myblood-Parameter.enemytorpedopower);
						Parameter.allbombs.get(i).setexist(false);
					}
				}
			}
			//detectbomb ? b_alert=true : b_alert=false;
			
			/**自己血量判断**/
			if(myblood<20)
			{
				//if(Sound==ON)
				//PlaySound(L"D:\\Submarine\\EXPLODE.WAV",NULL,SND_FILENAME | SND_ASYNC);
				if(Parameter.lives>1)
				{
					--Parameter.lives;
					me.setexploded(true);
					me.setblood(100);
				}
				else
				{
					MessageboxShowing=true;
					runOnUiThread(new Runnable()    
			        {    
			            public void run()    
			            {    
                        	new  AlertDialog.Builder(getContext())   
        					.setTitle("Message" )   
        					.setMessage("Game over.\nTry again?" )  
        					.setPositiveButton("Yes" ,  new DialogInterface.OnClickListener() {
        						@Override
        						public void onClick(DialogInterface dialog, int which) {
        							newgame();
        							return;
        						}
        					})   
        					.setNegativeButton("No" , new DialogInterface.OnClickListener() {
        						@Override
        						public void onClick(DialogInterface dialog, int which) {
        							Parameter.gameover=true;
        							return;
        						}
        					})   
        					.show(); 
			            }    
			        });    
				}
			}
			for(int i=0;i<Parameter.alltorpedos.size();i++)
				if(!Parameter.alltorpedos.get(i).getexist())
					Parameter.alltorpedos.removeElementAt(i);
			for(int i=0;i<Parameter.allverticaltorpedos.size();i++)
				if(!Parameter.allverticaltorpedos.get(i).getexist())
					Parameter.allverticaltorpedos.removeElementAt(i);
			for(int i=0;i<Parameter.allenemies.size();i++)
				if(!Parameter.allenemies.get(i).getexist())
					Parameter.allenemies.removeElementAt(i);
			for(int i=0;i<Parameter.allmissiles.size();i++)
				if(!Parameter.allmissiles.get(i).getexist())
					Parameter.allmissiles.removeElementAt(i);
			for(int i=0;i<Parameter.allbottom.size();i++)
				if(!Parameter.allbottom.get(i).getexist())
					Parameter.allbottom.removeElementAt(i);
			for(int i=0;i<Parameter.allships.size();i++)
				if(!Parameter.allships.get(i).getexist())
					Parameter.allships.removeElementAt(i);
			for(int i=0;i<Parameter.allbombs.size();i++)
				if(!Parameter.allbombs.get(i).getexist())
					Parameter.allbombs.removeElementAt(i);
			for(int i=0;i<Parameter.allawards.size();i++)
				if(!Parameter.allawards.get(i).getexist())
					Parameter.allawards.removeElementAt(i);
		}
		
		void changedifficulty()
		{
			if(Parameter.difficulty==Difficulty.Easy)
			{
				Parameter.maxenemies=10;
				Parameter.maxships=3;
				Parameter.maxmytorpedos=5;
				Parameter.enemytorpedospeed=20;
				Parameter.enemytorpedopower=20;
				Parameter.bombspeed=2;
			}
			else if(Parameter.difficulty==Difficulty.Normal)
			{
				Parameter.maxenemies=15;
				Parameter.maxships=4;
				Parameter.maxmytorpedos=2;
				Parameter.enemytorpedospeed=30;
				Parameter.enemytorpedopower=25;
				Parameter.bombspeed=3;
			}
			else if(Parameter.difficulty==Difficulty.Hard)
			{
				Parameter.maxenemies=20;
				Parameter.maxships=5;
				Parameter.maxmytorpedos=1;
				Parameter.enemytorpedospeed=40;
				Parameter.enemytorpedopower=50;
				Parameter.bombspeed=4;
			}
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event)
	     {
	         float x = event.getX();
	         float y = event.getY();
	         if(x>me.getx()-30 && x<me.getx()+130 && y>me.gety()-30 && y<me.gety()+70)
	        	 if(Parameter.missiletime==500)
	    		{
	    			me.sendmissile();
	    			Parameter.missilesent=true;
	    		}
	         if(y<200*Parameter.heightRatio && y>Parameter.barHeight)
	        	 me.launch(parameter.UP);
	         if(x>500*Parameter.widthRatio && y>200*Parameter.heightRatio)
	        	 me.launch(parameter.LEFT);
	         if(x>Pause_btn.x && x<Pause_btn.x+Pause_btn.size_x && y>Pause_btn.y && y<Pause_btn.y+Pause_btn.size_y && event.getAction()==MotionEvent.ACTION_DOWN)
	         {
	        		if(pause==false)
	        		{
	        			pause=true;//更改状态
	        			Pause_btn.touchon=true;//更改Pause按钮点击状态
	        			Bitmap origin = Bitmap.createBitmap(Parameter.screenWidth, Parameter.screenHeight, Bitmap.Config.ARGB_8888);
	        			Canvas bitCanvas = new Canvas(origin);
	        			draw(bitCanvas);//截图
	        			origin=toGrayscale(origin);//灰度转换
	        			Bitmap pauseImg=Bitmap.createBitmap(origin, 0, Parameter.barHeight, Parameter.screenWidth, Parameter.screenHeight-Parameter.barHeight);//裁剪保留状态栏以下部分
	        			/**绘制截图**/
	        			bitCanvas = mHolder.lockCanvas();
	        			bitCanvas.drawBitmap(pauseImg, 0, Parameter.barHeight, paint);
	        			Paint temppaint=new Paint();
	        			temppaint.setColor(Color.WHITE);
	        			temppaint.setTextSize(150*Parameter.heightRatio);
	        			bitCanvas.drawText("PAUSE", 400*Parameter.widthRatio, 400*Parameter.heightRatio, temppaint);
	        			Pause_btn.draw(bitCanvas);//Pause按钮表面颜色改变
	        			mHolder.unlockCanvasAndPost(bitCanvas);  
	        		}
	        		else
	        			pause=false;//更改状态
	         }
	         else 
	        	 Pause_btn.touchon=false;//更改Pause按钮点击状态
	         if(x>Quit_btn.x && x<Quit_btn.x+Quit_btn.size_x && y>Quit_btn.y && y<Quit_btn.y+Quit_btn.size_y && event.getAction()==MotionEvent.ACTION_DOWN)
	         {
	        	 setContentView(R.layout.activity_main);//返回主界面
	        	 newgame();//清空之前记录
	        	 contentviewID=1;//更改ID
	         }
	         return true;
	     }
		
	    Canvas canvas = null; 
		class MyThread implements Runnable { 
			@Override 
			public void run() {
				
				prepare();//加载图片资源
				
				while(IsRunning)
				{	
					/** 取得更新游戏之前的时间 **/
					//long startTime = System.currentTimeMillis();
					
					if(!pause)
					{
						produceenemy();
						produceaward();
						if(Parameter.missilesent)
							setmissile();

						/** 拿到当前画布 然后锁定 **/
						canvas = mHolder.lockCanvas();
						draw(canvas);
						/** 绘制结束后解锁显示在屏幕上 **/
						mHolder.unlockCanvasAndPost(canvas);
						if(!MessageboxShowing)
							check();
						if(Parameter.gameover)
						{
							begin();
							Parameter.gameover=false;
							newgame();
						}
						changedifficulty();
					}
					
					//Thread.yield();
					try {
						Thread.sleep(15);	
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					/** 取得更新游戏结束的时间 **/
//					long endTime = System.currentTimeMillis();
//					/** 计算出游戏一次更新的毫秒数 **/
//					long diffTime = endTime - startTime;
//					/** 确保每次更新时间为40帧 **/
//					while (diffTime <= TIME_IN_FRAME) {
//						diffTime = System.currentTimeMillis() - startTime;
//						/** 线程等待 **/
//						Thread.yield();
//					}
				}
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			IsRunning = false;
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			mGX = event.values[SensorManager.DATA_X];
			mGY= event.values[SensorManager.DATA_Y];
			mGZ = event.values[SensorManager.DATA_Z];
			if(mGX<-2 && me.getexploded()==false)
			{
				me.setdirectionY(parameter.UP);
				me.setv(parameter.maxspeed);
			}
			else if (mGX>2 && me.getexploded()==false) 
			{
				me.setdirectionY(parameter.DOWN);
				me.setv(parameter.maxspeed);
			}
			if(mGY<-2 && me.getexploded()==false)
			{
				me.setdirectionX(parameter.LEFT);
				me.setv(parameter.maxspeed);
			}
			else if (mGY>2 && me.getexploded()==false) 
			{
				me.setdirectionX(parameter.RIGHT);
				me.setv(parameter.maxspeed);
			}	
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {	
        	if(contentviewID==1)
        		java.lang.System.exit(0);//退出
        	else if(contentviewID==2)
        	{
        		setContentView(R.layout.activity_main);//返回主界面
        		newgame();//清空之前记录
        		contentviewID=1;//更改ID
        	}
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	public void start_click(View v)
	{
		setContentView(new MyView(this));
		contentviewID=2;//游戏界面ID为2
		pause=false;
	}
	
	public void exit_click(View v)
	{
		java.lang.System.exit(0);
	}
	
	public void about_click(View v)
	{
		new  AlertDialog.Builder(this)
		.setTitle("Submarine" )
		.setMessage("Copyright © Gu Jiawei 2014" )
		.setPositiveButton("OK",  null )
		.show();
	}
}
