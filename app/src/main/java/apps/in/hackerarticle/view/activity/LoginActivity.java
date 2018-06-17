package apps.in.hackerarticle.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import apps.in.hackerarticle.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    // [END declare_auth]
    public Unbinder unbinder;

    @BindView(R.id.phone)
    EditText mPhoneView;

    @BindView(R.id.til_otp)
    TextInputLayout tilOtp;

    @BindView(R.id.til_phone)
    TextInputLayout tilPhone;

    @BindView(R.id.otp)
    EditText mOtpView;

    @BindView(R.id.login_progress)
    View mProgressView;

    @BindView(R.id.login_form)
    View mLoginFormView;

    @BindView(R.id.sign_in_button)
    Button in_button;

    @BindView(R.id.verify_button)
    Button in_verify;

    private GoogleSignInClient mGoogleSignInClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Restore instance state
            if (savedInstanceState != null) {
                onRestoreInstanceState(savedInstanceState);
            }

            // Set up the login form.
            unbinder = ButterKnife.bind(this);

            // [START config_signin]
            // Configure Google Sign In
            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
            // [END config_signin]

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]

            // Initialize phone auth callbacks
            // [START phone_auth_callbacks]
            mCallbacks =
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            // This callback will be invoked in two situations:
                            // 1 - Instant verification. In some cases the phone number can be instantly
                            //     verified without needing to send or enter a verification code.
                            // 2 - Auto-retrieval. On some devices Google Play services can automatically
                            //     detect the incoming verification SMS and perform verification without
                            //     user action.
                            Log.d(TAG, "onVerificationCompleted:" + credential);
                            // [START_EXCLUDE silent]
                            mVerificationInProgress = false;
                            // [END_EXCLUDE]

                            // [START_EXCLUDE silent]
                            // Update the UI and attempt sign in with the phone credential
                            updateUI(STATE_VERIFY_SUCCESS, credential);
                            // [END_EXCLUDE]
                            signInWithPhoneAuthCredential(credential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            // This callback is invoked in an invalid request for verification is made,
                            // for instance if the the phone number format is not valid.
                            Log.w(TAG, "onVerificationFailed", e);
                            // [START_EXCLUDE silent]
                            mVerificationInProgress = false;
                            // [END_EXCLUDE]

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                // Invalid request
                                // [START_EXCLUDE]
                                mPhoneView.setError("Invalid phone number.");
                                // [END_EXCLUDE]
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                // The SMS quota for the project has been exceeded
                                // [START_EXCLUDE]
                                Snackbar.make(
                                        findViewById(android.R.id.content),
                                        "Quota exceeded.",
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                                // [END_EXCLUDE]
                            }

                            // Show a message and update the UI
                            // [START_EXCLUDE]
                            updateUI(STATE_VERIFY_FAILED);
                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCodeSent(
                                String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                            // The SMS verification code has been sent to the provided phone number, we
                            // now need to ask the user to enter the code and then construct a credential
                            // by combining the code with a verification ID.
                            Log.d(TAG, "onCodeSent:" + verificationId);
                            showProgress(false);
                            // Save verification ID and resending token so we can use them later
                            mVerificationId = verificationId;
                            mResendToken = token;

                            // [START_EXCLUDE]
                            // Update UI
                            updateUI(STATE_CODE_SENT);
                            // [END_EXCLUDE]
                        }
                    };
            // [END phone_auth_callbacks]
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneView.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhone.setError("Invalid phone number.");
            mPhoneView.requestFocus();
            return false;
        } else {
            mPhoneView.setError(null);
        }
        return true;
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneView.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
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
                    // [START_EXCLUDE]
                    updateUI(null);
                    // [END_EXCLUDE]
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]
            showProgress(true);
            // [END_EXCLUDE]

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth
                    .signInWithCredential(credential)
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        Snackbar.make(
                                                findViewById(R.id.main_layout),
                                                "Authentication Failed.",
                                                Snackbar.LENGTH_SHORT)
                                                .show();
                                        updateUI(null);
                                    }

                                    // [START_EXCLUDE]
                                    showProgress(false);
                                    // [END_EXCLUDE]
                                }
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // [END auth_with_google]

    // [START signin]
    @OnClick({R.id.google_button})
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        try {
            // [START start phone auth]
            PhoneAuthProvider.getInstance()
                    .verifyPhoneNumber(
                            phoneNumber, // Phone number to verify
                            60, // Timeout duration
                            TimeUnit.SECONDS, // Unit of timeout
                            this, // Activity (for callback binding)
                            mCallbacks); // OnVerificationStateChangedCallbacks
            // [END start phone auth]
            mVerificationInProgress = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(
                            new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                                }
                            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(
                            new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                                }
                            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @OnClick(R.id.verify_button)
    public void SmsVerifyTask() {
        // Store values at the time of the login attempt.
        String code = mOtpView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(code)) {
            tilOtp.setError(getString(R.string.error_incorrect_otp));
            mOtpView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            verifyPhoneNumberWithCode(mVerificationId, code);
        }
    }

    @OnClick(R.id.sign_in_button)
    public void SmsSentTask() {
        // Check for a valid password, if the user entered one.
        if (validatePhoneNumber()) {
            // perform the user login attempt.
            startPhoneNumberVerification(mPhoneView.getText().toString());
        }
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                showProgress(false);
                // Initialized state, show only the phone number field and start button
                tilOtp.setVisibility(View.GONE);
                tilPhone.setVisibility(View.VISIBLE);

                in_button.setVisibility(View.VISIBLE);
                in_verify.setVisibility(View.GONE);
                break;

            case STATE_CODE_SENT:
                showProgress(false);
                tilOtp.setVisibility(View.VISIBLE);
                tilPhone.setVisibility(View.GONE);

                in_button.setVisibility(View.GONE);
                in_verify.setVisibility(View.VISIBLE);
                break;

            case STATE_VERIFY_FAILED:
                showProgress(false);
                // Verification has failed, show all options
                tilOtp.setVisibility(View.GONE);
                tilPhone.setVisibility(View.VISIBLE);

                in_button.setVisibility(View.VISIBLE);
                in_verify.setVisibility(View.GONE);
                break;

            case STATE_VERIFY_SUCCESS:
                showProgress(true);
                // Verification has succeeded, proceed to firebase sign in
                tilOtp.setVisibility(View.GONE);
                tilPhone.setVisibility(View.GONE);

                in_button.setVisibility(View.GONE);
                in_verify.setVisibility(View.GONE);
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mOtpView.setText(cred.getSmsCode());
                    } else {
                        showProgress(false);
                        mOtpView.setText(R.string.instant_validation);
                    }
                }
                break;

            case STATE_SIGNIN_FAILED:
                showProgress(false);
                // No-op, handled by sign-in check
                break;

            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }
        if (user != null) gotoMainpage();
    }

    private void gotoMainpage() {
        try {
            startActivity(new Intent(this, ArticleActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(
            String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        mCallbacks, // OnVerificationStateChangedCallbacks
                        token); // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");

                                    FirebaseUser user = task.getResult().getUser();
                                    // [START_EXCLUDE]
                                    updateUI(STATE_SIGNIN_SUCCESS, user);
                                    // [END_EXCLUDE]
                                } else {
                                    // Sign in failed, display a message and update the UI
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        // The verification code entered was invalid
                                        // [START_EXCLUDE silent]
                                        tilOtp.setError("Invalid code.");
                                        // [END_EXCLUDE]
                                    }
                                    // [START_EXCLUDE silent]
                                    // Update UI
                                    updateUI(STATE_SIGNIN_FAILED);
                                    // [END_EXCLUDE]
                                }
                            }
                        });
    }
    // [END sign_in_with_phone]

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbinder.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
