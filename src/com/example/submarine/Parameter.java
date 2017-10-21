package com.example.submarine;

import java.util.Vector;

import com.example.submarine.Members.Award;
import com.example.submarine.Members.Bomb;
import com.example.submarine.Members.Bottom;
import com.example.submarine.Members.Label;
import com.example.submarine.Members.Missile;
import com.example.submarine.Members.Ship;
import com.example.submarine.Members.Submarine;
import com.example.submarine.Members.Torpedo;

import android.graphics.Bitmap;

public class Parameter {
	final int MYSELF = 0;
	final int ENEMY = 1;
	final int NONE = 0;
	final int UP = 1;
	final int DOWN = 2;
	final int LEFT = 3;
	final int RIGHT = 4;
	final static int OFF = 0;
	final static int ON = 1;
	final double PI = 3.1415927;
	final int SHIP = 1;
	final int SUBMARINE = 2;
	
	enum Difficulty{Easy,Normal,Hard};
	enum Select{Start,About,Exit};
	enum Mode{Level,Infinite};
	enum Bottomid{None,Grass1,Grass2,Grass3,Rock1,Rock2,Coral1,Coral2,Coral3};
	enum Type{None,Live,Blood,NewMissile,Allclear,Score};
	
	final int maxspeed=20;
	final int mytorpedospeed=30;
	final int acceleration=2;
	static int maxenemies=10;
	static int maxmytorpedos=5;
	static int enemytorpedospeed=20;
	static int enemies=0;
	static int mytorpedos=0;
	static int Effect=OFF;
	static boolean gameover=false;
	static int score=0;
	static int Sound=ON;
	static Difficulty difficulty=Difficulty.Easy;
	static int interval=20;
	static int lives=5;
	static int mytorpedopower=50;
	static int enemytorpedopower=20;
	static boolean start=false;
	static Mode mode=Mode.Level;
	static int level=1;
	static int missiletime=500;
	static boolean missilesent=false;
	static float flex=1;
	static int wavedisplacement=0;
	static int maxships=3;
	static int ships=0;
	static int bombspeed=2;
	static boolean newlevel=false;
	static int screenWidth=0;
	static int screenHeight=0;
	static float widthRatio=0;
	static float heightRatio=0;
	static int barHeight=0;
	
	static Bitmap MySubmarineImg;
	static Bitmap EnemySubmarine1Img;
	static Bitmap EnemySubmarine2Img;
	static Bitmap MyTorpedoImg;
	static Bitmap EnemyTorpedoImg;
	static Bitmap VerticalTorpedoImg;
	static Bitmap ExplosionImg;
	static Bitmap Coral1Img;
	static Bitmap Coral2Img;
	static Bitmap Coral3Img;
	static Bitmap Rock1Img;
	static Bitmap Rock2Img;
	static Bitmap Grass1Img;
	static Bitmap Grass2Img;
	static Bitmap Grass3Img;
	static Bitmap ShipImg;
	static Bitmap ShipImg_Rotated;
	static Bitmap BombImg;
	
	static Vector<Torpedo> alltorpedos=new Vector<Members.Torpedo>();
	static Vector<Torpedo> allverticaltorpedos=new Vector<Members.Torpedo>();
	static Vector<Missile> allmissiles=new Vector<Members.Missile>();
	static Vector<Bomb> allbombs=new Vector<Members.Bomb>();
	static Vector<Submarine> allenemies=new Vector<Members.Submarine>();
	static Vector<Bottom> allbottom=new Vector<Members.Bottom>();
	static Vector<Ship> allships=new Vector<Members.Ship>();
	static Vector<Label> alllabels=new Vector<Members.Label>();
	static Vector<Award> allawards=new Vector<Members.Award>();
}
