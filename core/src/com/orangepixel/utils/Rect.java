package com.orangepixel.utils;

public class Rect {

	public int left;
	public int right;
	public int top;
	public int bottom;
	public int width;
	public int height;
	
	public final void set(int mleft, int mtop, int mright, int mbottom) {
		left=mleft;
		top=mtop;
		right=mright;
		bottom=mbottom;
		
		width=right-left;
		height=bottom-top;
	}

	public final void set(Rect mSource) {
		left=mSource.left;
		top=mSource.top;
		right=mSource.right;
		bottom=mSource.bottom;
		width=mSource.width;
		height=mSource.height;
	}
	
}
