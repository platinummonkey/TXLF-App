package org.texaslinuxfest.txlfapp;

import java.util.ArrayList;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class AddContact extends Activity implements OnAccountsUpdateListener {
	// Couresty of http://developer.android.com/resources/samples/ContactManager/src/com/example/android/contactmanager/ContactAdder.html
	// Modified for TXLF
	
	public static final String LOG_TAG = "txlf - AddContact";
    public static final String ACCOUNT_NAME =
            "com.example.android.contactmanager.ContactsAdder.ACCOUNT_NAME";
    public static final String ACCOUNT_TYPE =
            "com.example.android.contactmanager.ContactsAdder.ACCOUNT_TYPE";

    private ArrayList<AccountData> mAccounts;
    private AccountAdapter mAccountAdapter;
    private Spinner mAccountSpinner;
    private EditText mContactEmailEditText;
    private EditText mContactWebsiteEditText;
    private EditText mContactOrganizationEditText;
    private EditText mContactWorkAddressEditText;
    private EditText mContactNameEditText;
    private EditText mContactWorkPhoneEditText;
    private EditText mContactHomePhoneEditText;
    private EditText mContactMobilePhoneEditText;
    private Button mContactSaveButton;
    private AccountData mSelectedAccount;
    private String iname;
    private String iw_phone;
    private String ih_phone;
    private String im_phone;
    private String iemail;
    private String iweb;
    private String iorganization;
    private String iw_address;
    
    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "Activity State: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_adder);
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
        	iname = extras.getString("name");
        	iw_phone = extras.getString("w_phone");
        	ih_phone = extras.getString("h_phone");
        	im_phone = extras.getString("m_phone");
        	iemail = extras.getString("email");
        	iweb = extras.getString("web");
        	iorganization = extras.getString("organization");
        	iw_address = extras.getString("w_address");
        }

        // Obtain handles to UI objects
        mAccountSpinner = (Spinner) this.findViewById(R.id.accountSpinner);
        mContactNameEditText = (EditText) this.findViewById(R.id.contactNameEditText);
        mContactWorkPhoneEditText = (EditText) this.findViewById(R.id.contactWorkPhoneEditText);
        mContactHomePhoneEditText = (EditText) this.findViewById(R.id.contactHomePhoneEditText);
        mContactMobilePhoneEditText = (EditText) this.findViewById(R.id.contactMobilePhoneEditText);
        mContactEmailEditText = (EditText) this.findViewById(R.id.contactEmailEditText);
        mContactWebsiteEditText = (EditText) this.findViewById(R.id.contactWebsiteEditText);
        mContactOrganizationEditText = (EditText) this.findViewById(R.id.contactOrganizationEditText);
        mContactWorkAddressEditText = (EditText) this.findViewById(R.id.contactWorkAddressEditText);
        mContactSaveButton = (Button) this.findViewById(R.id.contactSaveButton);
        

        /*// Prepare list of supported account types
        // Note: Other types are available in ContactsContract.CommonDataKinds
        //       Also, be aware that type IDs differ between Phone and Email, and MUST be computed
        //       separately.
        mContactPhoneTypes = new ArrayList<Integer>();
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
        mContactEmailTypes = new ArrayList<Integer>();
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_HOME);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_OTHER);*/

        // Prepare model for account spinner
        mAccounts = new ArrayList<AccountData>();
        mAccountAdapter = new AccountAdapter(this, mAccounts);
        mAccountSpinner.setAdapter(mAccountAdapter);

       /* // Populate list of account types for phone
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Iterator<Integer> iter;
        iter = mContactPhoneTypes.iterator();
        while (iter.hasNext()) {
            adapter.add(ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    this.getResources(),
                    iter.next(),
                    getString(R.string.undefinedTypeLabel)).toString());
        }
        mContactPhoneTypeSpinner.setAdapter(adapter);
        mContactPhoneTypeSpinner.setPrompt(getString(R.string.selectLabel));

        // Populate list of account types for email
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iter = mContactEmailTypes.iterator();
        while (iter.hasNext()) {
            adapter.add(ContactsContract.CommonDataKinds.Email.getTypeLabel(
                    this.getResources(),
                    iter.next(),
                    getString(R.string.undefinedTypeLabel)).toString());
        }
        mContactEmailTypeSpinner.setAdapter(adapter);
        mContactEmailTypeSpinner.setPrompt(getString(R.string.selectLabel));*/

        // Prepare the system account manager. On registering the listener below, we also ask for
        // an initial callback to pre-populate the account list.
        AccountManager.get(this).addOnAccountsUpdatedListener(this, null, true);

        //set default text on other items
        mContactNameEditText.setText(iname);
        mContactWorkPhoneEditText.setText(iw_phone);
        mContactHomePhoneEditText.setText(ih_phone);
        mContactMobilePhoneEditText.setText(im_phone);
        mContactEmailEditText.setText(iemail);
        mContactWebsiteEditText.setText(iweb);
        mContactOrganizationEditText.setText(iorganization);
        mContactWorkAddressEditText.setText(iw_address);
        
        // Register handlers for UI elements
        mAccountSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i) {
                updateAccountSelection();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // We don't need to worry about nothing being selected, since Spinners don't allow
                // this.
            }
        });
        mContactSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * Actions for when the Save button is clicked. Creates a contact entry and terminates the
     * activity.
     */
    private void onSaveButtonClicked() {
        Log.v(LOG_TAG, "Save button clicked");
        createContactEntry();
        finish();
    }

    /**
     * Creates a contact entry from the current UI values in the account named by mSelectedAccount.
     */
    protected void createContactEntry() {
        // Get values from UI
        String name = mContactNameEditText.getText().toString();
        String w_phone = mContactWorkPhoneEditText.getText().toString();
        String h_phone = mContactHomePhoneEditText.getText().toString();
        String m_phone = mContactMobilePhoneEditText.getText().toString();
        String email = mContactEmailEditText.getText().toString();
        String web = mContactEmailEditText.getText().toString();
        String organization = mContactEmailEditText.getText().toString();
        String w_address = mContactEmailEditText.getText().toString();
        
        // Prepare contact creation request
        //
        // Note: We use RawContacts because this data must be associated with a particular account.
        //       The system will aggregate this with any other data for this contact and create a
        //       coresponding entry in the ContactsContract.Contacts provider for us.
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, mSelectedAccount.getType())
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, mSelectedAccount.getName())
                .build());
        // name
        Log.i(LOG_TAG,"Adding name");
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        // work phone
        if (w_phone !=null) {
        	Log.i(LOG_TAG,"Adding work phone");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        			.withValue(ContactsContract.Data.MIMETYPE,
        					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        			.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, w_phone)
        			.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
        			.build());
        }
        // home phone
        if (h_phone !=null) {
        	Log.i(LOG_TAG,"Adding home phone");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        			.withValue(ContactsContract.Data.MIMETYPE,
        					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        			.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, h_phone)
        			.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
        			.build());
        }
        // mobile phone
        if (m_phone !=null) {
        	Log.i(LOG_TAG,"Adding mobile phone");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        			.withValue(ContactsContract.Data.MIMETYPE,
        					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        			.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, m_phone)
        			.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        			.build());
        }
        // email
        if (email !=null) {
        	Log.i(LOG_TAG,"Adding email");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        			.withValue(ContactsContract.Data.MIMETYPE,
        					ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        			.withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
        			.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
        			.build());
        }
        // website
        if (web !=null) {
        	Log.i(LOG_TAG,"Adding website");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
        			.withValue(ContactsContract.Data.MIMETYPE,
        					ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
        			.withValue(ContactsContract.CommonDataKinds.Website.URL, web)
        			.withValue(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_WORK)
        			.build());
        }
        // organization
        if (organization !=null) {
        	Log.i(LOG_TAG,"Adding organization");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, organization)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }
        // work address
        if (w_address !=null) {
        	Log.i(LOG_TAG,"Adding work address");
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, w_address)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                    .build());
        }

        // Ask the Contact provider to create a new contact
        Log.i(LOG_TAG,"Selected account: " + mSelectedAccount.getName() + " (" +
                mSelectedAccount.getType() + ")");
        Log.i(LOG_TAG,"Creating contact: " + name);
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            // Display warning
            Context ctx = getApplicationContext();
            CharSequence txt = getString(R.string.contactCreationFailure);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, txt, duration);
            toast.show();

            // Log exception
            Log.e(LOG_TAG, "Exceptoin encoutered while inserting contact: " + e);
        }
    }

    /**
     * Called when this activity is about to be destroyed by the system.
     */
    @Override
    public void onDestroy() {
        // Remove AccountManager callback
        AccountManager.get(this).removeOnAccountsUpdatedListener(this);
        super.onDestroy();
    }

    /**
     * Updates account list spinner when the list of Accounts on the system changes. Satisfies
     * OnAccountsUpdateListener implementation.
     */
    public void onAccountsUpdated(Account[] a) {
        Log.i(LOG_TAG, "Account list update detected");
        // Clear out any old data to prevent duplicates
        mAccounts.clear();

        // Get account data from system
        AuthenticatorDescription[] accountTypes = AccountManager.get(this).getAuthenticatorTypes();

        // Populate tables
        for (int i = 0; i < a.length; i++) {
            // The user may have multiple accounts with the same name, so we need to construct a
            // meaningful display name for each.
            String systemAccountType = a[i].type;
            AuthenticatorDescription ad = getAuthenticatorDescription(systemAccountType,
                    accountTypes);
            AccountData data = new AccountData(a[i].name, ad);
            mAccounts.add(data);
        }

        // Update the account spinner
        mAccountAdapter.notifyDataSetChanged();
    }

    /**
     * Obtain the AuthenticatorDescription for a given account type.
     * @param type The account type to locate.
     * @param dictionary An array of AuthenticatorDescriptions, as returned by AccountManager.
     * @return The description for the specified account type.
     */
    private static AuthenticatorDescription getAuthenticatorDescription(String type,
            AuthenticatorDescription[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            if (dictionary[i].type.equals(type)) {
                return dictionary[i];
            }
        }
        // No match found
        throw new RuntimeException("Unable to find matching authenticator");
    }

    /**
     * Update account selection. If NO_ACCOUNT is selected, then we prohibit inserting new contacts.
     */
    private void updateAccountSelection() {
        // Read current account selection
        mSelectedAccount = (AccountData) mAccountSpinner.getSelectedItem();
    }

    /**
     * A container class used to repreresent all known information about an account.
     */
    private class AccountData {
        private String mName;
        private String mType;
        private CharSequence mTypeLabel;
        private Drawable mIcon;

        /**
         * @param name The name of the account. This is usually the user's email address or
         *        username.
         * @param description The description for this account. This will be dictated by the
         *        type of account returned, and can be obtained from the system AccountManager.
         */
        public AccountData(String name, AuthenticatorDescription description) {
            mName = name;
            if (description != null) {
                mType = description.type;

                // The type string is stored in a resource, so we need to convert it into something
                // human readable.
                String packageName = description.packageName;
                PackageManager pm = getPackageManager();

                if (description.labelId != 0) {
                    mTypeLabel = pm.getText(packageName, description.labelId, null);
                    if (mTypeLabel == null) {
                        throw new IllegalArgumentException("LabelID provided, but label not found");
                    }
                } else {
                    mTypeLabel = "";
                }

                if (description.iconId != 0) {
                    mIcon = pm.getDrawable(packageName, description.iconId, null);
                    if (mIcon == null) {
                        throw new IllegalArgumentException("IconID provided, but drawable not " +
                                "found");
                    }
                } else {
                    mIcon = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
                }
            }
        }

        public String getName() {
            return mName;
        }

        public String getType() {
            return mType;
        }

        public CharSequence getTypeLabel() {
            return mTypeLabel;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public String toString() {
            return mName;
        }
    }

    /**
     * Custom adapter used to display account icons and descriptions in the account spinner.
     */
    private class AccountAdapter extends ArrayAdapter<AccountData> {
        public AccountAdapter(Context context, ArrayList<AccountData> accountData) {
            super(context, android.R.layout.simple_spinner_item, accountData);
            setDropDownViewResource(R.layout.account_entry);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            // Inflate a view template
            if (convertView == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.account_entry, parent, false);
            }
            TextView firstAccountLine = (TextView) convertView.findViewById(R.id.firstAccountLine);
            TextView secondAccountLine = (TextView) convertView.findViewById(R.id.secondAccountLine);
            ImageView accountIcon = (ImageView) convertView.findViewById(R.id.accountIcon);

            // Populate template
            AccountData data = getItem(position);
            firstAccountLine.setText(data.getName());
            secondAccountLine.setText(data.getTypeLabel());
            Drawable icon = data.getIcon();
            if (icon == null) {
                icon = getResources().getDrawable(android.R.drawable.ic_menu_search);
            }
            accountIcon.setImageDrawable(icon);
            return convertView;
        }
    }

}
