package com.gaurav.studyapp.developement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.menu.AddAssignments;
import com.gaurav.studyapp.developement.menu.AddPracticles;
import com.gaurav.studyapp.developement.menu.Home;
import com.gaurav.studyapp.developement.menu.MessageToAdmin;
import com.gaurav.studyapp.developement.menu.ShareApp;
import com.gaurav.studyapp.developement.menu.UsersFriends;
import com.gaurav.studyapp.developement.menu.UserMessages;
import com.gaurav.studyapp.developement.menu.UserProfile;
import com.gaurav.studyapp.developement.menu.UserQuestionAndSuggestions;
import com.gaurav.studyapp.developement.saveddata.LocalServerKey;
import com.gaurav.studyapp.developement.server.FirebaseDatabaseServerKey;
import com.gaurav.studyapp.developement.server.ServerKeyData;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.gaurav.studyapp.developement.utils.CircularImage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.File;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  private GoogleApiClient mGoogleSignInClient;
  private TextView userCollegeNameTextView, userNameTextView;
  private ImageView userImageView;
  ServerKeyData serverKeyData = new ServerKeyData();
  LocalServerKey localServerKey = new LocalServerKey();
  FirebaseDatabaseServerKey serverKey = new FirebaseDatabaseServerKey();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //---------------------- server key check --------------------------------------------------
      serverKey.setServerKey();
    String server_key_from_getter_setter = serverKeyData.getServer_key();
      Log.d("fcm_server_key","server_key_from_getter_setter"+server_key_from_getter_setter);
      String server_key_from_local_storage = localServerKey.getServerKeyFromLocalStorage(getApplicationContext());
      Log.d("fcm_server_key","server_key_from_local_storage"+server_key_from_local_storage);
      //--------------------------------------------------------------------------------------------

      subscribeToCollegeTopic();

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    View headerView = navigationView.getHeaderView(0);
    navigationView.setNavigationItemSelectedListener(this);
    userCollegeNameTextView  = headerView.findViewById(R.id.user_college_name_text_view);
    userNameTextView = headerView.findViewById(R.id.user_name_text_view);
    userImageView = headerView.findViewById(R.id.user_image_view);
    //----------------------------------------Google Sign in --------------------------------
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
              @Override
              public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(HomeActivity.this, "Log in error!", Toast.LENGTH_SHORT).show();
              }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build();
    //--------------------------------------------------------------------------------------------------------
    navigationSet();
    displaySelectedScreen(R.id.home_menu);
  }

    public void subscribeToCollegeTopic(){
        //----------------- Subscribe to topic -------------------------------------------------
        try {
            String collegeName = CollegeNameGetterSetter.getCollegeName();
            String remove_space = collegeName.replace(" ", "");
            String topic_name = remove_space.trim();
            FirebaseMessaging.getInstance().subscribeToTopic(topic_name);
            Log.d("subscription","topic_name"+topic_name);
        }catch (Exception e){
            Log.d("subscription","Exception: "+e.toString());
        }
//-------------------------------------------------------------------------------------------------
    }

  public void navigationSet(){
    UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    userNameTextView.setText(usersDetails.getPersonName());
    Picasso.get().load(usersDetails.getPersonImage()).transform(new CircularImage()).into(userImageView);
    final DatabaseReference collegeRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(usersDetails.getPersonId());
    final DatabaseReference reference = collegeRef.child("college_selected").getRef();
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String name = (String) dataSnapshot.child("users_college").getValue();
        userCollegeNameTextView.setText(name);
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
    super.onBackPressed();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.sign_out_menu) {
      FirebaseAuth.getInstance().signOut();
      mGoogleSignInClient.clearDefaultAccountAndReconnect();
      Intent a = new Intent(Intent.ACTION_MAIN);
      a.addCategory(Intent.CATEGORY_HOME);
      a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(a);
      deleteCache(getApplicationContext());
    }

    return super.onOptionsItemSelected(item);
  }

  public static void deleteCache(Context context) {
    try {
      File dir = context.getCacheDir();
      deleteDir(dir);
    } catch (Exception e) {}
  }

  public static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
      return dir.delete();
    } else if(dir!= null && dir.isFile()) {
      return dir.delete();
    } else {
      return false;
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    displaySelectedScreen(item.getItemId());

    return true;
  }

  private void displaySelectedScreen(int id){
    Fragment fragment = null;
    switch (id) {
      case R.id.home_menu:
        fragment = new Home();
        break;
      case R.id.user_profile_menu:
        fragment = new UserProfile();
        break;
      case R.id.user_messages_menu:
        fragment = new UserMessages();
        break;
      case R.id.add_assignment_menu:
        fragment = new AddAssignments();
        break;
      case R.id.add_practicle_menu:
        fragment = new AddPracticles();
        break;
      case R.id.users_friends_menu:
        fragment = new UsersFriends();
        break;
      case R.id.share_app:
        fragment = new ShareApp();
        break;
      case R.id.user_suggestion_menu:
        fragment = new UserQuestionAndSuggestions();
        break;
      default:
    }

    if (fragment != null) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.content_frame, fragment);
      ft.commit();
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
  }

}
