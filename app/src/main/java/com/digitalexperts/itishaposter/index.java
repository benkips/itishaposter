package com.digitalexperts.itishaposter;

        import android.Manifest;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.view.MenuItem;
        import android.view.View;

        import androidx.annotation.NonNull;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.core.view.GravityCompat;
        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;
        import androidx.navigation.ui.AppBarConfiguration;
        import androidx.navigation.ui.NavigationUI;

        import com.google.android.material.navigation.NavigationView;

        import androidx.drawerlayout.widget.DrawerLayout;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;

        import android.view.Menu;
        import android.webkit.JavascriptInterface;
        import android.widget.TextView;
        import android.widget.Toast;

public class index extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView cuser;
    private SharedPreferences preferences;
    public static TextView notitext;
    public static String x="0" ;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawer;
    private  View actionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences=getSharedPreferences("logininfo.conf",MODE_PRIVATE);
        String phn=preferences.getString("phone","");

         drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
       /* setupbadge("");*/
        View NavHeader=navigationView.getHeaderView(0);
        cuser= NavHeader.findViewById(R.id.cinfo);
        cuser.setText(phn);
        storageperm();


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main, R.id.nav_payments,R.id.nav_profile,
                R.id.nav_about,R.id.notification)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_contact){
                    toolbar.setTitle("Itisha Poster");
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String p="+254711114002";
                    intent.setData(Uri.parse("tel:"+p));
                    startActivity(intent);
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        getMenuInflater().inflate(R.menu.cartview, menu);
        final MenuItem menuItemt=menu.findItem(R.id.ntfction);
        actionView= menuItemt.getActionView();
        notitext=(TextView)actionView.findViewById(R.id.noti_badge);
        cleartext();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItemt);

            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.ntfction){

            NavController navController=Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.notification);

        }else if(id==R.id.action_settings){
            editor = preferences.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(index.this, login.class));
            index.this.finish();
        }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }



    public static void setupbadge(String cart) {

        if (!cart.equals("0")) {
            notitext.setText(cart);
            if (notitext.getVisibility() != View.VISIBLE) {
                notitext.setVisibility(View.VISIBLE);
            }
        }else{
            if (notitext.getVisibility() != View.INVISIBLE) {
                notitext.setVisibility(View.INVISIBLE);
            }
        }
    }
    private  void cleartext(){
        String txt=notitext.getText().toString().trim();
        if(txt.equals("0")) {
            notitext.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    private  void storageperm(){
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(index.this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(index.this, permission_list, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    storageperm();
                    Toast.makeText(index.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
