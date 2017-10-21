package com.example.submarine;

import com.example.submarine.Parameter.Bottomid;
import com.example.submarine.Parameter.Type;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class Members {
	public Parameter parameter=new Parameter();

	public class object
	{
		int getx()
		{
			return x;
		}
		int gety()
		{
			return y;
		}
		boolean getexist()
		{
			return exist;
		}
		void setexist(boolean e)
		{
			exist=e;
		}
		int x;
		int y;
		boolean exist;
	}

	class Missile extends object
	{
		Missile(int _x,int _y)
		{
			x=_x;
			y=_y;
			exist=true;
		}
		void draw(Canvas canvas)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(0.06f, 0.06f);// 缩放原图
			matrix.postRotate((int)(360*angle/parameter.PI));//旋转图片
			missileimg_rotated = Bitmap.createBitmap(Parameter.MyTorpedoImg, 0, 0, Parameter.MyTorpedoImg.getWidth(), Parameter.MyTorpedoImg.getHeight(),matrix, true);
			canvas.drawBitmap(missileimg_rotated, x, y, null);
		}
		void move()
		{
			x+=(int)(parameter.mytorpedospeed*Math.cos(angle));
			y+=(int)(parameter.mytorpedospeed*Math.sin(angle));
			if(x>Parameter.screenWidth || y<0 || y>Parameter.screenHeight)
				exist=false;
		}
		void setangle(double angle2)
		{
			angle=angle2;
		}
		double angle;
		Bitmap missileimg_rotated;
	}
	
	class Torpedo extends object
	{
		Torpedo(int _x,int _y,int _identity,int _direction)
		{
			x=_x;
			y=_y;
			identity=_identity;
			direction=_direction;
			exist=true;
			alarmplayed=false;
		}
		void draw(Canvas canvas)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(0.06f, 0.06f);// 缩放原图
			if(direction==parameter.LEFT)
			{
				if(identity==parameter.MYSELF)
				{
					Bitmap dstbmp = Bitmap.createBitmap(Parameter.MyTorpedoImg, 0, 0, Parameter.MyTorpedoImg.getWidth(), Parameter.MyTorpedoImg.getHeight(),matrix, true);
					canvas.drawBitmap(dstbmp, x, y, null);
				}
				else if(identity==parameter.ENEMY)
				{
					Bitmap dstbmp = Bitmap.createBitmap(Parameter.EnemyTorpedoImg, 0, 0, Parameter.EnemyTorpedoImg.getWidth(), Parameter.EnemyTorpedoImg.getHeight(),matrix, true);
					canvas.drawBitmap(dstbmp, x, y, null);
				}
			}
			else if(direction==parameter.RIGHT)
			{
				Bitmap dstbmp = Bitmap.createBitmap(Parameter.MyTorpedoImg, 0, 0, Parameter.MyTorpedoImg.getWidth(), Parameter.MyTorpedoImg.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, y, null);
			}
			else if(direction==parameter.UP)
			{
				Bitmap dstbmp = Bitmap.createBitmap(Parameter.VerticalTorpedoImg, 0, 0, Parameter.VerticalTorpedoImg.getWidth(), Parameter.VerticalTorpedoImg.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, y, null);
			}
		}
		void move()
		{
			if(direction==parameter.LEFT)
			{
				if(identity==parameter.MYSELF)
					x+=parameter.mytorpedospeed;
				else if(identity==parameter.ENEMY)
					x-=Parameter.enemytorpedospeed;
				if(x>Parameter.screenWidth)
				{
					exist=false;
					--Parameter.mytorpedos;
				}
				else if(x<-38)
					exist=false;
			}
			else if(direction==parameter.RIGHT)
			{
				x+=parameter.mytorpedospeed;
				if(x>Parameter.screenWidth)
					exist=false;
			}
			else if(direction==parameter.UP)
			{
				y-=parameter.mytorpedospeed;
				if(y<150*Parameter.heightRatio)
				{
					exist=false;
					--Parameter.mytorpedos;
				}
			}
		}
		int getidentity()
		{
			return identity;
		}
		int getdirection()
		{
			return direction;
		}
		boolean getalarmplayed()
		{
			return alarmplayed;
		}
		void setalarmplayed(boolean a)
		{
			alarmplayed=a;
		}
		int identity;
		int direction;
		boolean alarmplayed;
	}

	public class Submarine extends object
	{
			Submarine(int _x,int _y,int _identity)
			{
				x=_x;
				y=_y;
				identity=_identity;
				directionX=parameter.NONE;
				directionY=parameter.NONE;
				v=parameter.maxspeed;
				exist=true;
				exploded=false;
				computertime=0;
				exptime=0;
				adding=true;
				y_adding=0;
				blood=100;
				hit=false;
				meposition=parameter.LEFT;
			}
			void setdirectionX(int d)
			{
				directionX=d;
			}
			void setdirectionY(int d)
			{
				directionY=d;
			}
			void explode(double n)
			{
//				static double dx=0;
//				static double dy=0;
//				for(double i=1;i<=n;i+=1)
//				{
//					if(exptime<10)
//					{
//						dx=exptime*cos(i/n*2*PI)*2;
//						dy=exptime*sin(i/n*2*PI)*2;
//					}
//					else
//					{
//						dx=10*cos(i/n*2*PI)*2;
//						dy=10*sin(i/n*2*PI)*2;
//					}
//					setfillcolor(YELLOW);
//					solidcircle(x+43+(int)dx,y+15+(int)dy,5);
//				}
				//floodfill(x+43,y+15,YELLOW);
			}
			void draw(Canvas canvas)
			{
				Matrix matrix = new Matrix();
				matrix.postScale(0.16f, 0.16f);// 缩放原图
				if(!adding)
				{
					if(!exploded)
					{
						if(identity==parameter.MYSELF)
						{
							Bitmap bmp=Parameter.MySubmarineImg;
							Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
							canvas.drawBitmap(dstbmp, x, y, null);
						}
						else if(identity==parameter.ENEMY)
						{
							if(meposition==parameter.LEFT)
							{
								Bitmap bmp=Parameter.EnemySubmarine1Img;
								Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
								canvas.drawBitmap(dstbmp, x, y, null);
							}
							else if(meposition==parameter.RIGHT)
							{
								Bitmap bmp=Parameter.EnemySubmarine2Img;
								Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
								canvas.drawBitmap(dstbmp, x, y, null);
							}
						}
						Paint paint=new Paint();
						if(blood>=50) 
							paint.setColor(Color.rgb((int)(-5.1*blood+510),255,0));
						else  
							paint.setColor(Color.rgb(255,(int)(5.1*blood),0));
						RectF rect=new RectF(x,y-20*Parameter.heightRatio,x+123*Parameter.widthRatio*blood/100,y-10*Parameter.heightRatio);
						canvas.drawRect(rect, paint);
					}
					else
					{
						++exptime;
						if(Parameter.Effect==Parameter.ON)
							explode(15);
						else
						{
							Bitmap bmp=Parameter.ExplosionImg;
							Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
							canvas.drawBitmap(dstbmp, x, y, null);
						}
						if(exptime==50)
						{
							if(identity==parameter.ENEMY)
								exist=false;
							else if(identity==parameter.MYSELF)
							{
								exploded=false;
								exptime=0;
							}
						}
					}
				}
				else
				{
					if(y_adding<y-100*Parameter.heightRatio)
						y_adding+=parameter.maxspeed*2;
					else if(y_adding>=y-100*Parameter.heightRatio && y_adding<y-20*Parameter.heightRatio)
					{
						v-=parameter.acceleration;
						y_adding+=v;
					}
					else
					{
						adding=false;
						v=parameter.maxspeed;
					}
					if(identity==parameter.MYSELF)
					{
						Bitmap bmp=Parameter.MySubmarineImg;
						Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
						canvas.drawBitmap(dstbmp, x, y, null);
					}
					else if(identity==parameter.ENEMY)
					{
						if(meposition==parameter.LEFT)
						{
							Bitmap bmp=Parameter.EnemySubmarine1Img;
							Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
							canvas.drawBitmap(dstbmp, x, y, null);
						}
						else if(meposition==parameter.RIGHT)
						{
							Bitmap bmp=Parameter.EnemySubmarine2Img;
							Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
							canvas.drawBitmap(dstbmp, x, y, null);
						}
					}
				}
			}
			void move()
			{
				if(directionY==parameter.UP && y-parameter.maxspeed>200*Parameter.heightRatio)
				{	
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						y-=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							directionY=parameter.NONE;
						}
					}
					else
					{
						y-=parameter.maxspeed;
						directionY=parameter.NONE;
					}
				}
				else if(directionY==parameter.DOWN && y+parameter.maxspeed<680*Parameter.heightRatio)
				{
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						y+=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							directionY=parameter.NONE;
						}
					}
					else
					{
						y+=parameter.maxspeed;
						directionY=parameter.NONE;
					}
				}
				if(directionX==parameter.LEFT && x-parameter.maxspeed>0)
				{
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						x-=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							directionX=parameter.NONE;
						}
					}
					else
					{
						x-=parameter.maxspeed;
						directionX=parameter.NONE;
					}
				}
				else if(directionX==parameter.RIGHT && x+parameter.maxspeed<1150*Parameter.widthRatio)
				{
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						x+=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							directionX=parameter.NONE;
						}
					}
					else
					{
						x+=parameter.maxspeed;
						directionX=parameter.NONE;
					}
				}
			}
			void setx(int _x)
			{
				x=_x;
			}
			void sety(int _y)
			{
				y=_y;
			}
			void launch(int direction)
			{
				if(direction==parameter.LEFT)
				{
					if(identity==parameter.MYSELF && Parameter.mytorpedos<Parameter.maxmytorpedos)
					{
						Torpedo t=new Torpedo((int)(x+58*Parameter.widthRatio),(int)(y+20*Parameter.heightRatio),parameter.MYSELF,parameter.LEFT);
						Parameter.alltorpedos.add(t);
						++Parameter.mytorpedos;
					}
					else if(identity==parameter.ENEMY)
					{
						Torpedo t=new Torpedo(x,(int)(y+20*Parameter.heightRatio),parameter.ENEMY,parameter.LEFT);
						Parameter.alltorpedos.add(t);
					}
				}
				else if(direction==parameter.RIGHT)
				{
					Torpedo t=new Torpedo(x,(int)(y+20*Parameter.heightRatio),parameter.ENEMY,parameter.RIGHT);
					Parameter.alltorpedos.add(t);
				}
				else if(direction==parameter.UP && Parameter.mytorpedos<Parameter.maxmytorpedos)
				{
					Torpedo t=new Torpedo((int)(x+58*Parameter.widthRatio),(int)(y+20*Parameter.heightRatio),parameter.MYSELF,parameter.UP);
					Parameter.allverticaltorpedos.add(t);
					++Parameter.mytorpedos;
				}
			}
			void setv(int _v)
			{
				v=_v;
			}
			void setexploded(boolean ex)
			{
				exploded=ex;
			}	
			boolean getexploded()
			{
				return exploded;
			}
			void setcomputertime(int t)
			{
				computertime=t;
			}
			int getcomputertime()
			{
				return computertime;
			}
			void setblood(int b)
			{
				blood=b;
			}
			int getblood()
			{
				return blood;
			}
			void sendmissile()
			{
				Missile m=new Missile((int)(x+58*Parameter.widthRatio),(int)(y+20*Parameter.heightRatio));
				Parameter.allmissiles.add(m);
				Parameter.missiletime=0;
			}
			void sethit(boolean h)
			{
				hit=h;
			}
			boolean gethit()
			{
				return hit;
			}
			void setmeposition(int m)
			{
				meposition=m;
			}
			int getmeposition()
			{
				return meposition;
			}
			int identity;
			int directionX;
			int directionY;
			int v;
			boolean exploded;
			int computertime;
			int exptime;
			boolean adding;
			int y_adding;
			int blood;
			boolean hit;
			int meposition;
	}

	public class Bottom extends object
	{
		Bottom(Bottomid _id)
		{
			x=Parameter.screenWidth;
			id=_id;
			exist=true;
		}
		void draw(Canvas canvas)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(0.14f, 0.14f);// 缩放原图
			Matrix matrix2 = new Matrix();
			matrix2.postScale(0.23f, 0.23f);// 缩放珊瑚
			Bitmap bmp;
			Bitmap dstbmp;
			switch(id)
			{
			case Grass1:
				bmp=Parameter.Grass1Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Grass2:
				bmp=Parameter.Grass2Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Grass3:
				bmp=Parameter.Grass3Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Rock1:
				bmp=Parameter.Rock1Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Rock2:
				bmp=Parameter.Rock2Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Coral1:
				bmp=Parameter.Coral1Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix2, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Coral2:
				bmp=Parameter.Coral2Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix2, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case Coral3:
				bmp=Parameter.Coral3Img;
				dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix2, true);
				canvas.drawBitmap(dstbmp, x, 640*Parameter.heightRatio, null);
				break;
			case None:
				break;
			}
		}
		void move()
		{
			x--;
			if(x<-20)
				exist=false;
		}
		void setid(Bottomid _id)
		{
			id=_id;
		}
		int x;
		Bottomid id;
	}

	public class Bomb extends object
	{
		Bomb(int _x,int _y)
		{
			x=_x;
			y=_y;
			exist=true;
			exploded=false;
			exptime=0;
			expplayed=false;
		}
		void explode(double n)
		{
//			static double dx=0;
//			static double dy=0;
//			for(double i=1;i<=n;i+=1)
//			{
//				if(exptime<10)
//				{
//					dx=exptime*Math.cos(i/n*2*parameter.PI)*2;
//					dy=exptime*Math.sin(i/n*2*parameter.PI)*2;
//				}
//				else
//				{
//					dx=10*Math.cos(i/n*2*parameter.PI)*2;
//					dy=10*Math.sin(i/n*2*parameter.PI)*2;
//				}
//				setfillcolor(YELLOW);
//				solidcircle(x+6+(int)dx,y+10+(int)dy,5);
//			}
//			floodfill(x+6,y+10,YELLOW);
		}
		void draw(Canvas canvas)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(0.06f, 0.06f);// 缩放原图
			if(!exploded)
			{
				Bitmap bmp=Parameter.BombImg;
				Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, y, null);
			}
			else
			{
				++exptime;
				if(Parameter.Effect==Parameter.ON)
					explode(15);
				else
				{
					Bitmap bmp=Parameter.ExplosionImg;
					Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
					canvas.drawBitmap(dstbmp, x, y, null);
				}
				if(exptime==50)
					exist=false;
			}
		}
		void move()
		{	
			if(y>680*Parameter.heightRatio)
			{
				if(Parameter.Sound==Parameter.ON && !expplayed)
				{
					expplayed=true;
					//PlaySound(L"D:\\Submarine\\EXPLODE.WAV",NULL,SND_FILENAME | SND_ASYNC);
				}
				exploded=true;
			}
			else
				y+=Parameter.bombspeed;
		}
		boolean getexist()
		{
			return exist;
		}
		void setexploded(boolean ex)
		{
			exploded=ex;
		}	
		boolean getexploded()
		{
			return exploded;
		}
		boolean getalarmplayed()
		{
			return alarmplayed;
		}
		void setalarmplayed(boolean a)
		{
			alarmplayed=a;
		}
		boolean exploded;
		int exptime;
		boolean expplayed;
		boolean alarmplayed;
	}

	public class Ship extends object
	{
		Ship(int _x)
		{
			x=_x;
			y=(int)(Parameter.flex*(float)Math.sin(0.01*(float)(x+52+Parameter.wavedisplacement))+63*Parameter.heightRatio);
			direction=parameter.NONE;
			v=parameter.maxspeed;
			exist=true;
			exploded=false;
			computertime=0;
			exptime=0;
			blood=100;
		}
		void setdirection(int d)
		{
			direction=d;
		}
		void move()
		{
			if(!exploded)
			{
				if(direction==parameter.LEFT && x-parameter.maxspeed>0)
				{
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						x-=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							direction=parameter.NONE;
						}
					}
					else
					{
						x-=parameter.maxspeed;
						direction=parameter.NONE;
					}
				}
				else if(direction==parameter.RIGHT && x+parameter.maxspeed<680*Parameter.widthRatio)
				{
					if(Parameter.Effect==Parameter.ON)
					{
						v-=parameter.acceleration;
						x+=v;
						if(v<=0)
						{
							v=parameter.maxspeed;
							direction=parameter.NONE;
						}
					}
					else
					{
						x+=parameter.maxspeed;
						direction=parameter.NONE;
					}
				}
				y=(int)(Parameter.flex*(float)Math.sin(0.01*(float)(x+52+Parameter.wavedisplacement))+100*Parameter.heightRatio);
			}
			else
				y++;
		}
		void explode(double n)
		{
//			static double dx=0;
//			static double dy=0;
//			for(double i=1;i<=n;i+=1)
//			{
//				if(exptime<10)
//				{
//					dx=exptime*cos(i/n*2*PI)*2;
//					dy=exptime*sin(i/n*2*PI)*2;
//				}
//				else
//				{
//					dx=10*cos(i/n*2*PI)*2;
//					dy=10*sin(i/n*2*PI)*2;
//				}
//				setfillcolor(YELLOW);
//				solidcircle(x+51+(int)dx,y+26+(int)dy,5);
//			}
//			floodfill(x+51,y+26,YELLOW);
		}
		void draw(Canvas canvas)
		{
			Matrix matrix = new Matrix();
			matrix.postScale(0.16f, 0.16f);// 缩放原图
			if(!exploded)
			{
				Bitmap bmp=Parameter.ShipImg;
				Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, y, null);		
				Paint paint=new Paint();
				if(blood>=50) 
					paint.setColor(Color.rgb((int)(-5.1*blood+510),255,0));
				else  
					paint.setColor(Color.rgb(255,(int)(5.1*blood),0));
				RectF rect=new RectF(x,y-20*Parameter.heightRatio,x+133*Parameter.widthRatio*blood/100,y-10*Parameter.heightRatio);
				canvas.drawRect(rect, paint);
			}
			else
			{
				++exptime;
				Bitmap bmp=Parameter.ShipImg_Rotated;
				Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),matrix, true);
				canvas.drawBitmap(dstbmp, x, y-10*Parameter.heightRatio, null);
				if(Parameter.Effect==Parameter.ON)
					explode(15);
				else
				{
					Bitmap bmp2=Parameter.ExplosionImg;
					Bitmap dstbmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(),matrix, true);
					canvas.drawBitmap(dstbmp2, x, y, null);
				}
				if(y>=150*Parameter.heightRatio)
					exist=false;
			}
		}
		void launch()
		{
			Bomb b=new Bomb((int)(x+50*Parameter.widthRatio),(int)(y+30*Parameter.heightRatio));
			Parameter.allbombs.add(b);
		}
		void setx(int _x)
		{
			x=_x;
		}
		void sety(int _y)
		{
			y=_y;
		}
		void setv(int _v)
		{
			v=_v;
		}
		void setexploded(boolean ex)
		{
			exploded=ex;
		}	
		boolean getexploded()
		{
			return exploded;
		}
		void setcomputertime(int t)
		{
			computertime=t;
		}
		int getcomputertime()
		{
			return computertime;
		}
		void setblood(int b)
		{
			blood=b;
		}
		int getblood()
		{
			return blood;
		}
		int direction;
		int v;
		boolean exploded;
		int computertime;
		int exptime;
		int blood;
	}

	public class Label extends object
	{
		Label(int l)
		{
			level=l;
			y=0;
			time=0;
			exist=true;
		}
		void draw(Canvas canvas)
		{
			if(time<50)
			{
				Paint paint=new Paint(); 
				paint.setColor(Color.rgb(120,0,255));
				RectF rect=new RectF(580*Parameter.widthRatio,y,730*Parameter.widthRatio,y+60*Parameter.heightRatio);
				canvas.drawRoundRect(rect, 10*Parameter.widthRatio, 10*Parameter.heightRatio, paint);
				paint.setColor(Color.YELLOW);
				paint.setTextSize(34*Parameter.heightRatio);
				paint.setTypeface(Typeface.DEFAULT_BOLD);
				canvas.drawText("Level "+Parameter.level, 598*Parameter.widthRatio, y+40*Parameter.heightRatio, paint);
			}
			else
			{
				exist=false;
				Parameter.newlevel=false;
			}
		}
		void move()
		{
			if(y<355*Parameter.heightRatio)
				y+=10;
			else
				time++;
		}
		void reset()
		{
			y=0;
			time=0;
			exist=true;
		}
		int level;
		int time;
	}

	public class Award extends object
	{
		Award(int _y,Type t)
		{
			x=Parameter.screenWidth;
			y=_y;
			type=t;
			exist=true;
		}
		void move()
		{
			x--;
			if(x<0)
				exist=false;
		}
		void draw(Canvas canvas)
		{
			Paint paint=new Paint(); 
			paint.setColor(Color.rgb(255,60,60));
			canvas.drawCircle(x, y, 16, paint);
			paint.setTextSize(24);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setColor(Color.WHITE);
			final int dx=10;
			final int dy=-7;
			switch(type)
			{
			case Allclear:canvas.drawText("A", x-dx*Parameter.widthRatio, y-dy*Parameter.heightRatio, paint);break;
			case Blood:canvas.drawText("B", x-dx*Parameter.widthRatio, y-dy*Parameter.heightRatio, paint);break;
			case Live:canvas.drawText("L", x-dx*Parameter.widthRatio, y-dy*Parameter.heightRatio, paint);break;
			case NewMissile:canvas.drawText("M", x-dx*Parameter.widthRatio, y-dy*Parameter.heightRatio, paint);break;
			case Score:canvas.drawText("S", x-dx*Parameter.widthRatio, y-dy*Parameter.heightRatio, paint);break;
			default:break;
			}
		}
		int getx()
		{
			return x;
		}
		int gety()
		{
			return y;
		}
		Type gettype()
		{
			return type;
		}
		Type type;
	}
	
	class Control
	{
		public int x;
		public int y;
		public int size_x;
		public int size_y;
		public int text_x;
		public int text_y;
		public boolean touchon;
	}
	
	class Button extends Control
	{
		Button()
		{
			touchon=false;
		}
		void draw(Canvas canvas)
		{
			Paint paint=new Paint();
			if(!touchon) 
				paint.setColor(color_normal);
			else
				paint.setColor(color_on);
			Rect rect=new Rect(x,y,x+size_x,y+size_y);
			canvas.drawRect(rect, paint);
			paint.setTextSize(size_y-12*Parameter.heightRatio);
			paint.setColor(color_text);
			canvas.drawText(text, text_x,text_y, paint);
		}
		public String text;
		public int color_normal;
		public int color_on;
		public int color_text;
	};

}

