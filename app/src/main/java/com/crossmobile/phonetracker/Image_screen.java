package com.crossmobile.phonetracker;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.crossmobile.phonetracker.R;

public class Image_screen extends Activity {

    ImageView view1, view2, view3, view4, view5, view6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_screen);
        view1 = (ImageView)findViewById(R.id.imageView);
        view1.setImageURI(Uri.parse("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&docid=FoRv9Ribu_DbGM&tbnid=GmknswJeZHBa9M:&ved=0CAUQjRw&url=http%3A%2F%2Fwww.picturesnew.com%2Fcars.html&ei=zMjtU-2ZCoGBiwLumIHICw&bvm=bv.73231344,d.cGU&psig=AFQjCNEeSklFhRQBpebphSXk06eIwNptjg&ust=1408178759723902"));

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view2 = (ImageView)findViewById(R.id.imageView2);
        view2.setImageURI(Uri.parse("http://www.google.com/imgres?imgurl=http%3A%2F%2Fimages2.layoutsparks.com%2F1%2F108075%2F350z-cars-stylish-modified.jpg&imgrefurl=http%3A%2F%2Fwww.layoutsparks.com%2Fpictures%2Fcars-0&h=559&w=800&tbnid=xMFlTT1oy7BWVM%3A&zoom=1&docid=abC_7NL8t-uySM&ei=x8jtU-aUGMr8oATli4DYDw&tbm=isch&ved=0CJYBEDMoXDBc&iact=rc&uact=3&dur=1328&page=6&start=79&ndsp=16"));

        view3 = (ImageView)findViewById(R.id.imageView3);
        view3.setImageURI(Uri.parse("http://www.google.com/imgres?imgurl=http%3A%2F%2Fcdn.wonderfulengineering.com%2Fwp-content%2Fuploads%2F2014%2F07%2FCar-Wallpapers-15.jpg&imgrefurl=http%3A%2F%2Fwonderfulengineering.com%2F49-speedy-car-wallpapers-for-free-desktop-download%2F&h=1080&w=1920&tbnid=q2y4JnIjpuDGiM%3A&zoom=1&docid=KLG0WF91S9WJSM&ei=x8jtU-aUGMr8oATli4DYDw&tbm=isch&ved=0CJsBEDMoYTBh&iact=rc&uact=3&dur=1403&page=7&start=95&ndsp=16"));

        view4 = (ImageView)findViewById(R.id.imageView4);
        view4.setImageURI(Uri.parse("http://www.google.com/imgres?imgurl=http%3A%2F%2Fcrispme.com%2Fwp-content%2Fuploads%2F2012%2F12%2FBMW-i8.jpg&imgrefurl=http%3A%2F%2Fcrispme.com%2F25-awesome-car-wallpapers-2%2F&h=1080&w=1920&tbnid=v4cCtr4JeLV6jM%3A&zoom=1&docid=PUKDvdjmu6vcsM&ei=x8jtU-aUGMr8oATli4DYDw&tbm=isch&ved=0CJUBEDMoWzBb&iact=rc&uact=3&dur=834&page=6&start=79&ndsp=16"));

        view5 = (ImageView)findViewById(R.id.imageView5);
        view5.setImageURI(Uri.parse("http://www.google.com/imgres?imgurl=https%3A%2F%2Fs2.yimg.com%2Fbt%2Fapi%2Fres%2F1.2%2FLs7hKn8LftzMCrM1Il4n.g--%2FYXBwaWQ9eW5ld3M7cT04NTt3PTMxMA--%2Fhttp%3A%2F%2Fl.yimg.com%2Fos%2Fpublish-images%2Fautos%2F2013-05-15%2F1a483582-3be7-44af-ac68-5b7dc1257688_coolest-cop-cars-02-0513-lgn.jpg&imgrefurl=https%3A%2F%2Fautos.yahoo.com%2Fnews%2Fthe-10-coolest-high-performance-cop-cars-011049791.html&h=233&w=310&tbnid=h2RZJQTwQrCZAM%3A&zoom=1&docid=0Nsl3Z5aofvLpM&ei=8MntU6b2Jo7ZoATr0IKwBg&tbm=isch&ved=0CBAQMygIMAg4ZA&iact=rc&uact=3&dur=1038&page=7&start=95&ndsp=16"));

        view6 = (ImageView)findViewById(R.id.imageView6);
        view6.setImageURI(Uri.parse("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&docid=FoRv9Ribu_DbGM&tbnid=GmknswJeZHBa9M:&ved=0CAUQjRw&url=http%3A%2F%2Fwww.picturesnew.com%2Fcars.html&ei=zMjtU-2ZCoGBiwLumIHICw&bvm=bv.73231344,d.cGU&psig=AFQjCNEeSklFhRQBpebphSXk06eIwNptjg&ust=1408178759723902"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
