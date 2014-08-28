package com.nl.clubbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 11.08.2014.
 */
public class TestActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test);

        findViewById(R.id.txtShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
                intent.putExtra(Intent.EXTRA_TEXT,"Extra Text");

                startActivity(Intent.createChooser(intent, "Share to"));
            }
        });
    }

    ////    private UiLifecycleHelper uiHelper;
////    private Session.StatusCallback callback = new Session.StatusCallback() {
////        @Override
////        public void call(final Session session, final SessionState state, final Exception exception) {
////            onSessionStateChange(session, state, exception);
////        }
////    };
//
//    private TextView userInfoTextView;
//    private boolean isReopened = false;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ac_test);
//
//        userInfoTextView = (TextView) findViewById(R.id.textUserInfo);
//
////        uiHelper = new UiLifecycleHelper(TestActivity.this, callback);
////        uiHelper.onCreate(savedInstanceState);
//
//        findViewById(R.id.txtSignUp).setOnClickListener(this);
//    }
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////
////        uiHelper.onResume();
////    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Session.getActiveSession().onActivityResult(TestActivity.this, requestCode, resultCode, data);
////        uiHelper.onActivityResult(requestCode, resultCode, data);
////
////        Session.getActiveSession().
//    }
//
////    @Override
////    public void onPause() {
////        super.onPause();
////        uiHelper.onPause();
////    }
////
////    @Override
////    public void onDestroy() {
////        super.onDestroy();
////        uiHelper.onDestroy();
////    }
////
////    @Override
////    public void onSaveInstanceState(Bundle outState) {
////        super.onSaveInstanceState(outState);
////        uiHelper.onSaveInstanceState(outState);
////    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.txtSignUp) {
//            onBtnClicked();
//        }
//    }
//
//    private void onBtnClicked() {
//
////        Session.openActiveSession(this, true, new Session.StatusCallback() {
////            @Override
////            public void call(Session session, SessionState state, Exception exception) {
////                if (session.isOpened()) {
////
////                    Session.getActiveSession().openForRead(new Session.OpenRequest(TestActivity.this)
////                                    .setPermissions(Arrays.asList("public_profile", "email", "user_birthday"))
////                                    .setCallback(new Session.StatusCallback() {
////                                        @Override
////                                        public void call(Session session, SessionState state, Exception exception) {
////                                            if (state.isOpened()) {
////                                                Request.newMeRequest(session, new Request.GraphUserCallback() {
////
////                                                    @Override
////                                                    public void onCompleted(GraphUser user, Response response) {
////                                                        if (user != null) {
////                                                            userInfoTextView.setText("Hello " + user.getName() + "!");
////                                                            userInfoTextView.append("\nEmail " + user.asMap().get("email"));
////                                                            userInfoTextView.append("\nUser birthday " + user.asMap().get("user_birthday"));
////                                                        }
////                                                    }
////                                                }).executeAsync();
////                                            }
////                                        }
////                                    })
////                    );
////                }
////            }
////        });
//
////            Session.openForRead(new Session.OpenRequest(TestActivity.this)
////                    .setPermissions(Arrays.asList("public_profile", "email", "user_birthday"))
////                    .setCallback(new Session.StatusCallback() {
////                        @Override
////                        public void call(Session session, SessionState state, Exception exception) {
////                            if(state.isOpened()) {
////                                Request.newMeRequest(session, new Request.GraphUserCallback() {
////
////                                    @Override
////                                    public void onCompleted(GraphUser user, Response response) {
////                                        if (user != null) {
////                                            userInfoTextView.setText("Hello " + user.getName() + "!");
////                                            userInfoTextView.append("\nEmail " + user.asMap().get("email"));
////                                            userInfoTextView.append("\nUser birthday " + user.asMap().get("user_birthday"));
////                                        }
////                                    }
////                                }).executeAsync();
////                            }
////                        }
////                    })
////            );
//
//            Session.openActiveSession(this, true, new Session.StatusCallback() {
//                @Override
//                public void call(Session session, SessionState state, Exception exception) {
//                    if (session.isOpened()) {
//
//                        L.i("session.isOpened()");
//                        L.w("permissions - " + session.getPermissions());
//
//                        if(!session.getPermissions().containsAll(Arrays.asList("email"))) {
//                            L.e("!contains");
//                            session.requestNewReadPermissions(new Session.NewPermissionsRequest(TestActivity.this,
//                                    Arrays.asList("email", "user_birthday")));
//                        } else {
//                            Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                                @Override
//                                public void onCompleted(GraphUser user, Response response) {
//                                    if (user != null) {
//                                        userInfoTextView.setText("Hello " + user.getName() + "!");
//                                        userInfoTextView.append("\nEmail " + user.asMap().get("email"));
//                                        userInfoTextView.append("\nUser birthday " + user.asMap().get("user_birthday"));
//                                    }
//                                }
//                            }).executeAsync();
//                        }
//                    }
//                }
//            });
//    }
//
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (state.isOpened()) {
//            userInfoTextView.setVisibility(View.VISIBLE);
//
//
//            L.e("isOpened");
////            session.requestNewReadPermissions(new Session.NewPermissionsRequest(TestActivity.this,
////                    Arrays.asList("public_profile", "email", "user_birthday")));
////
////            Request.newMeRequest(session, new Request.GraphUserCallback() {
////
////                @Override
////                public void onCompleted(GraphUser user, Response response) {
////                    if (user != null) {
////                        userInfoTextView.setText("Hello " + user.getName() + "!");
////                        userInfoTextView.append("\nEmail " + user.asMap().get("email"));
////                        userInfoTextView.append("\nUser birthday " + user.asMap().get("user_birthday"));
////                    }
////                }
////            }).executeAsync();
//        } else if (state.isClosed()) {
//
//            L.e("isClosed");
////            userInfoTextView.setVisibility(View.INVISIBLE);
//        }
//    }


    //    private UiLifecycleHelper uiHelper;
//    private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(final Session session, final SessionState state, final Exception exception) {
//            onSessionStateChange(session, state, exception);
//        }
//    };
//
//    private TextView userInfoTextView;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ac_test);
//
//        uiHelper = new UiLifecycleHelper(TestActivity.this, callback);
//        uiHelper.onCreate(savedInstanceState);
//
//        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
//        authButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
//
//        userInfoTextView = (TextView) findViewById(R.id.textUserInfo);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // For scenarios where the main activity is launched and user
//        // session is not null, the session state change notification
//        // may not be triggered. Trigger it if it's open/closed.
//        Session session = Session.getActiveSession();
//        if (session != null &&
//                (session.isOpened() || session.isClosed()) ) {
//            onSessionStateChange(session, session.getState(), null);
//        }
//
//        uiHelper.onResume();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        uiHelper.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        uiHelper.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        uiHelper.onDestroy();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
//    }
//
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (state.isOpened()) {
//            userInfoTextView.setVisibility(View.VISIBLE);
//
//            // Request user data and show the results
//            Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                @Override
//                public void onCompleted(GraphUser user, Response response) {
//                    L.e("response - " + response);
//
//                    if (user != null) {
//                        // Display the parsed user info
//                        userInfoTextView.setText(buildUserInfoDisplay(user));
//                    }
//                }
//            }).executeAsync();
//        } else if (state.isClosed()) {
//            userInfoTextView.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private String buildUserInfoDisplay(GraphUser user) {
//        StringBuilder userInfo = new StringBuilder("");
//
//        // Example: typed access (name)
//        // - no special permissions required
//        userInfo.append(String.format("Name: %s\n\n",
//                user.getName()));
//
//        // Example: typed access (birthday)
//        // - requires user_birthday permission
//        userInfo.append(String.format("Birthday: %s\n\n",
//                user.getBirthday()));
//
////        // Example: partially typed access, to location field,
////        // name key (location)
////        // - requires user_location permission
////        userInfo.append(String.format("Location: %s\n\n",
////                user.getLocation().getProperty("name")));
////
////        // Example: access via property name (locale)
////        // - no special permissions required
////        userInfo.append(String.format("Locale: %s\n\n",
////                user.getProperty("locale")));
////
////        // Example: access via key for array (languages)
////        // - requires user_likes permission
////
////        // Option 3: Get the language data from the typed interface and after
////        // sub-classing GraphUser object to get at the languages.
////        GraphObjectList<MyGraphLanguage> languages = (user.cast(MyGraphUser.class)).getLanguages();
////        if (languages.size() > 0) {
////            ArrayList<String> languageNames = new ArrayList<String> ();
////            for (MyGraphLanguage language : languages) {
////                // Add the language name to a list. Use the name
////                // getter method to get access to the name field.
////                languageNames.add(language.getName());
////            }
////            userInfo.append(String.format("Languages: %s\n\n",
////                    languageNames.toString()));
////        }
//
//        // Option2: Get the data from creating a typed interface
//        // for the language data
////        JSONArray languages = (JSONArray)user.getProperty("languages");
////        if (languages.length() > 0) {
////            ArrayList<String> languageNames = new ArrayList<String> ();
////
////            // Get the data from creating a typed interface
////            // for the language data.
////            GraphObjectList<MyGraphLanguage> graphObjectLanguages =
////            	GraphObject.Factory.createList(languages,
////            			MyGraphLanguage.class);
////
////            // Iterate through the list of languages
////            for (MyGraphLanguage language : graphObjectLanguages) {
////            	// Add the language name to a list. Use the name
////                // getter method to get access to the name field.
////            	languageNames.add(language.getName());
////            }
////
////            userInfo.append(String.format("Languages: %s\n\n",
////            languageNames.toString()));
////        }
//
//        // Option 1: Get the data from parsing JSON
////        JSONArray languages = (JSONArray)user.getProperty("languages");
////        if (languages.length() > 0) {
////            ArrayList<String> languageNames = new ArrayList<String> ();
////
////            for (int i=0; i < languages.length(); i++) {
////                JSONObject language = languages.optJSONObject(i);
////                languageNames.add(language.optString("name"));
////            }
////
////            userInfo.append(String.format("Languages: %s\n\n",
////            languageNames.toString()));
////        }
//
//        return userInfo.toString();
//    }
//
//    // Private interface for GraphUser that includes
//    // the languages field: Used in Option 3
//    private interface MyGraphUser extends GraphUser {
//        // Create a setter to enable easy extraction of the languages field
//        GraphObjectList<MyGraphLanguage> getLanguages();
//    }
//
//    // Private interface for a language Graph Object
//    // for a User: Used in Options 2 and 3
//    private interface MyGraphLanguage extends GraphObject {
//        // Getter for the ID field
//        String getId();
//        // Getter for the Name field
//        String getName();
//    }
}
