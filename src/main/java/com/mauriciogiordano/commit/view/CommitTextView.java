package com.mauriciogiordano.commit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.mauriciogiordano.commit.R;

public class CommitTextView extends android.widget.TextView
{
	private String SCHEMA = "http://schemas.android.com/apk/lib/com.mauriciogiordano.commit.view";
	private Context context = null;
	private int textThickness = 4;
	private String textFor = "";
	
    public CommitTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.context = context;
        
        isInEditMode();
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommitTextView);

        try {
        	try
        	{
        		textThickness = Integer.parseInt(attrs.getAttributeValue(SCHEMA, "textThickness"));
        	} catch(NumberFormatException e)
        	{
        		textThickness = 4;
        	}
        	
        	textFor = attrs.getAttributeValue(SCHEMA, "textFor");
        	
        	if(textFor == null) textFor = "p";

            setThickness(textThickness);
            setTextUtility(textFor);

            invalidate();
            requestLayout();
	   	} finally {
	   		a.recycle();
   		}
    }

    private void setThickness(int textThickness)
    {
    	Typeface typeface = null;
    	
        switch(textThickness)
        {
        case 0:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_black.ttf");
        	break;
        case 1:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        	break;
        case 2:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
        	break;
        case 3:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        	break;
        case 4:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        	break;
        case 5:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        	break;
        default:
        	typeface = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        	break;
        }

        setTypeface(typeface);
    }
    
    private void setTextUtility(String textFor)
    {
    	if(textFor.equals("goku"))
    		setTextSize(getResources().getDimension(R.dimen.textview_goku));
    	else if(textFor.equals("hero"))
    		setTextSize(getResources().getDimension(R.dimen.textview_hero));
    	else if(textFor.equals("h1"))
    		setTextSize(getResources().getDimension(R.dimen.textview_h1));
    	else if(textFor.equals("h2"))
    		setTextSize(getResources().getDimension(R.dimen.textview_h2));
    	else if(textFor.equals("h3"))
    		setTextSize(getResources().getDimension(R.dimen.textview_h3));
    	else if(textFor.equals("p"))
    		setTextSize(getResources().getDimension(R.dimen.textview_p));
    	else if(textFor.equals("small"))
    		setTextSize(getResources().getDimension(R.dimen.textview_small));
    	else
    		setTextSize(getResources().getDimension(R.dimen.textview_p));
    }
}