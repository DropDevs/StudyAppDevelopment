package com.gaurav.studyapp.developement;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.gaurav.studyapp.R;
import com.gaurav.studyapp.developement.firebasedata.CollegeNameGetterSetter;
import com.gaurav.studyapp.developement.firebasedata.GetUserCollegeName;
import com.gaurav.studyapp.developement.server.FirebaseDatabaseServerKey;
import com.gaurav.studyapp.developement.usersdata.UsersDetails;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.registerable.connection.Connectable;

import io.fabric.sdk.android.Fabric;

/**
 * @author Gaurav
 */
public class LogInActivity extends AppCompatActivity {

  private static final String TAG = "Error";
  private SignInButton googleSignInButton;
  private static final int RC_SIGN_IN = 1;
  private GoogleApiClient mGoogleSignInClient;
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthStateListener;
  private DatabaseReference googleUserRef;
  private String personName;
  private String personEmail;
  private String personId;
  private ProgressBar circularProBar;
  private Merlin merlin;
  FirebaseDatabaseServerKey serverKey = new FirebaseDatabaseServerKey();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    setContentView(R.layout.activity_log_in);

    circularProBar = findViewById(R.id.circular_pro_bar);
    circularProBar.setVisibility(View.INVISIBLE);
    googleSignInButton = findViewById(R.id.google_login_button);

    merlin = new Merlin.Builder().withConnectableCallbacks().build(getApplicationContext());
    merlin.registerConnectable(new Connectable() {
      @Override
      public void onConnect() {
        Toast.makeText(LogInActivity.this, "Internet Available", Toast.LENGTH_SHORT).show();
      }
    });

    googleUserRef = FirebaseDatabase.getInstance().getReference().child("user_info");
    mAuth = FirebaseAuth.getInstance();
    mAuthStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (mAuth.getCurrentUser() != null){
            //-------------- Set firebase server key--------------------------------
            initFCM();

            //---------------------------------------------------------
          circularProBar.setVisibility(View.VISIBLE);
          googleSignInButton.setVisibility(View.INVISIBLE);
          try {
            GetUserCollegeName collegeName = new GetUserCollegeName();
            collegeName.getCollegeName(getApplicationContext());
          }catch (Exception e){
            Log.d("Error",e.toString());
          }
          UsersDetails usersDetails = new UsersDetails(getApplicationContext());
          final DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference()
                  .child("user_info")
                  .child(usersDetails.getPersonId())
                  .getRef();
          checkRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild("college_selected")){
                collegeFount(checkRef);
              }else if (!dataSnapshot.hasChild("college_selected")){
                collegeNotFount(checkRef);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });

        }
      }
    };

    // Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
              @Override
              public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(LogInActivity.this, "Log in error!", Toast.LENGTH_SHORT).show();
              }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build();

    googleSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signIn();
      }
    });

  }




  private void signIn() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = task.getResult(ApiException.class);

        firebaseAuthWithGoogle(account);
      } catch (ApiException e) {
        // Google Sign In failed, update UI appropriately
        Log.w(TAG, "Google sign in failed", e);
        // ...
      }
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthStateListener);
  }

  private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                  if (acct != null) {
                    personName = acct.getDisplayName();
                    personEmail = acct.getEmail();
                    personId = acct.getId();
                    final String image = acct.getPhotoUrl().toString();
                    final DatabaseReference reference = googleUserRef.child(personId);
                    googleUserRef.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference.child("name").setValue(personName);
                        reference.child("email").setValue(personEmail);
                        reference.child("id").setValue(personId);
                        if (image != null) {
                          reference.child("image").setValue(image);

                        }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                    });

                  }
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithCredential:success");
                  FirebaseUser user = mAuth.getCurrentUser();
                  mAuth.addAuthStateListener(mAuthStateListener);
                } else {
                  // If sign in fails, display a message to the user.
                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                  Toast.makeText(LogInActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                }


              }
            }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {

      }
    });
  }


  public void collegeFount(DatabaseReference checkRef){
    checkRef
            .child("college_selected")
            .getRef();
    checkRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String collegeName = (String) dataSnapshot.child("users_college").getValue();
        Intent intent = new Intent(LogInActivity.this,HomeActivity.class);
        intent.putExtra("users_college",collegeName);
        startActivity(intent);
        finish();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void initFCM(){
    final UsersDetails usersDetails = new UsersDetails(getApplicationContext());
    Task<InstanceIdResult> userRefreshedToken = FirebaseInstanceId.getInstance().getInstanceId();
    userRefreshedToken.addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
      @Override
      public void onSuccess(InstanceIdResult instanceIdResult) {
        String refreshedToken = instanceIdResult.getToken();
        sendTokenToDatabase(refreshedToken,usersDetails.getPersonId());
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d("fcm","get_fcm_token_exception: "+e.toString());
      }
    });
  }

  public void sendTokenToDatabase(final String userToken, String personId){
    final DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference()
            .child("user_info")
            .child(personId)
            .getRef();
    tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        tokenRef.child("user_refreshed_token").setValue(userToken).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
           Log.d("token_send","Token_saved_to_database");
          }
        });
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void collegeNotFount(DatabaseReference reference){
    Intent toCollegeSelectActivity = new Intent(LogInActivity.this,CollegeSelectActivity.class);
    startActivity(toCollegeSelectActivity);
    finish();
  }

  @Override
  public void onBackPressed() {
    Intent a = new Intent(Intent.ACTION_MAIN);
    a.addCategory(Intent.CATEGORY_HOME);
    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(a);
    super.onBackPressed();
  }

  @Override
  protected void onResume() {
    super.onResume();
    merlin.bind();
  }

  @Override
  protected void onPause() {
    merlin.unbind();
    super.onPause();
  }

}
