package com.mechatronics.felinks;

import com.mechatronics.felinks.R;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.ToggleButton;

/**
 * This is the main activity of the Felinks example application.
 * 
 * It displays a toggle button on the screen, which enables control of the on-board LED. This example shows a very simple usage of the IOIO, by using the {@link IOIOActivity} class. For a more advanced use case, see the FelinksPower example.
 */
public class MainActivity extends IOIOActivity
{
  private ToggleButton on_off_button;
  private SeekBar seek1;
  private SeekBar seek2;
  private TextView left_output_text;
  private TextView right_output_text;
  private TextView top_seek_label;
  private TextView bottom_seek_label;
  private LinearLayout seek_bar_box;
  
  private RadioButton manual;
  private RadioButton joy;
  
  int[] joy_coords = new int[2];
  private int x;
  private int y;
  
  private int left_output;
  private int right_output;
  
  private boolean changing_modes;
  
  /**
   * Called when the activity is first created. Here we normally initialize our GUI.
   */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    on_off_button = (ToggleButton) findViewById(R.id.button);
    seek1 = (SeekBar) findViewById(R.id.seekBar1);
    seek2 = (SeekBar) findViewById(R.id.seekBar2);
    left_output_text = (TextView) findViewById(R.id.left_output);
    right_output_text = (TextView) findViewById(R.id.right_output);
    top_seek_label = (TextView) findViewById(R.id.seekBar1Label);
    bottom_seek_label = (TextView) findViewById(R.id.seekBar2Label);
    manual = (RadioButton) findViewById(R.id.radioManual);
    joy = (RadioButton) findViewById(R.id.radioJoy);
    seek_bar_box = (LinearLayout) findViewById(R.id.seek_bar_box);
    
    changing_modes = false;
    
    seek_bar_box.getLocationOnScreen(joy_coords);
    
    // update UI on radio button change
    ((RadioGroup) findViewById(R.id.control_mode_switch)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
    {
      public void onCheckedChanged(RadioGroup group, int checkedId)
      {
        updateUI();
      }
    });
    
    seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
      {
        calculateOutputs();
      }
      
      @Override
      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void onStopTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        calculateOutputs();
      }
    });
    
    seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
      {
        calculateOutputs();
      }
      
      @Override
      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void onStopTrackingTouch(SeekBar seekBar)
      {
        // TODO Auto-generated method stub
        calculateOutputs();
      }
    });
    
    updateUI();
  }
  
  public void updateUI()
  {
    changing_modes = true;
    if(manual.isChecked())
    {
      seek1.setProgress(left_output + 1000);
      seek2.setProgress(right_output + 1000);
      top_seek_label.setText("Left (" + (seek1.getProgress() - 1000) + ")");
      bottom_seek_label.setText("Right (" + (seek2.getProgress() - 1000) + ")");
      
      seek1.setEnabled(true);
      seek2.setEnabled(true);
      seek1.setVisibility(SeekBar.VISIBLE);
      seek2.setVisibility(SeekBar.VISIBLE);
      top_seek_label.setVisibility(TextView.VISIBLE);
      bottom_seek_label.setVisibility(TextView.VISIBLE);
      seek_bar_box.setBackgroundColor(Color.rgb(0, 0, 0));
      seek_bar_box.setOnTouchListener(new OnTouchListener()
      {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
          
          return false;
        }
      });
    }
    else if(joy.isChecked())
    {
      seek1.setEnabled(false);
      seek2.setEnabled(false);
      seek1.setVisibility(SeekBar.INVISIBLE);
      seek2.setVisibility(SeekBar.INVISIBLE);
      top_seek_label.setVisibility(TextView.INVISIBLE);
      bottom_seek_label.setVisibility(TextView.INVISIBLE);
      seek_bar_box.setBackgroundColor(Color.rgb(127, 127, 127));
      seek_bar_box.setOnTouchListener(new OnTouchListener()
      {
        
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
          x = 2000 - (int) (2 * ((double) ((int) event.getX()) - joy_coords[0]) / v.getWidth() * 1000.0);
          y = 2000 - (int) (2 * ((double) ((int) event.getY()) - joy_coords[1]) / v.getHeight() * 1000.0);
          calculateOutputs();
          return true;
        }
      });
    }
    changing_modes = false;
  }
  
  public int clamp(int input, int clampValue)
  {
    int clamped = input;
    if(Math.abs(input) > clampValue)
    {
      clamped = (int) (clampValue * Math.signum((double) input));
    }
    return clamped;
  }
  
  public void calculateOutputs()
  {
    if(changing_modes)
    {
      return;
    }
    
    if(manual.isChecked())
    {
      left_output = seek1.getProgress() - 1000; // seek bar goes from 0 to 2000
      right_output = seek2.getProgress() - 1000;
      top_seek_label.setText("Left (" + (seek1.getProgress() - 1000) + ")");
      bottom_seek_label.setText("Right (" + (seek2.getProgress() - 1000) + ")");
    }
    else if(joy.isChecked())
    {
      int base_power = y - 1000;
      int offset = (x - 1000) / 2;
      left_output = base_power - offset; // seek bar goes from 0 to 2000
      right_output = base_power + offset;
    }
    left_output = clamp(left_output,1000);
    right_output = clamp(right_output,1000);
    
    left_output_text.setText(((double) left_output) / 10.0 + "%");
    right_output_text.setText(((double) right_output) / 10.0 + "%");
    
    if(left_output < 0)
    {
      left_output_text.setTextColor(Color.rgb(127, 0, 0));
    }
    else
    {
      left_output_text.setTextColor(Color.rgb(0, 127, 0));
    }
    
    if(right_output < 0)
    {
      right_output_text.setTextColor(Color.rgb(127, 0, 0));
    }
    else
    {
      right_output_text.setTextColor(Color.rgb(0, 127, 0));
    }
  }
  
  /**
   * This is the thread on which all the IOIO activity happens. It will be run every time the application is resumed and aborted when it is paused. The method setup() will be called right after a connection with the IOIO has been established (which might happen several times!). Then, loop() will be called repetitively until the IOIO gets disconnected.
   */
  class Looper extends BaseIOIOLooper
  {
    /** The on-board LED. */
    private DigitalOutput led_;
    private DigitalOutput left_dir;
    private DigitalOutput right_dir;
    private PwmOutput left;
    private PwmOutput right;
    
    private static final int frequency = 1000;
    
    /**
     * Called every time a connection with IOIO has been established. Typically used to open pins.
     * 
     * @throws ConnectionLostException
     *           When IOIO connection is lost.
     * 
     * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
     */
    @Override
    protected void setup() throws ConnectionLostException
    {
      led_ = ioio_.openDigitalOutput(0, true);
      right_dir = ioio_.openDigitalOutput(43, true);
      left_dir = ioio_.openDigitalOutput(44, true);
      right = ioio_.openPwmOutput(45, frequency);
      left = ioio_.openPwmOutput(46, frequency);
    }
    
    /**
     * Called repetitively while the IOIO is connected.
     * 
     * @throws ConnectionLostException
     *           When IOIO connection is lost.
     * 
     * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
     */
    @Override
    public void loop() throws ConnectionLostException
    {
      if(on_off_button.isChecked())
      {
        led_.write(true);
        left.setDutyCycle(0);
        right.setDutyCycle(0);
      }
      else
      {
        led_.write(false);
        
        left.setPulseWidth(Math.abs(left_output));
        right.setPulseWidth(Math.abs(right_output));
        left_dir.write(left_output > 0);
        right_dir.write(right_output > 0);
      }
      
      try
      {
        Thread.sleep(100);
      }
      catch(InterruptedException e)
      {
      }
    }
  }
  
  /**
   * A method to create our IOIO thread.
   * 
   * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
   */
  @Override
  protected IOIOLooper createIOIOLooper()
  {
    return new Looper();
  }
}