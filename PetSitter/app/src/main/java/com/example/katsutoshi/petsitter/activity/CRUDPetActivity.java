package com.example.katsutoshi.petsitter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katsutoshi.petsitter.R;
import com.example.katsutoshi.petsitter.model.Medication;
import com.example.katsutoshi.petsitter.model.Pet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.util.UUID.randomUUID;

public class CRUDPetActivity extends AppCompatActivity {


    private TextView selectedPetName;
    private EditText name, weight, birth, race, genre, restrictions, qnt, desc;
    private String child;
    private String state;

    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mDBReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_crudpet);

        Bundle bundle = getIntent().getExtras();
        state = bundle.getString("state");
        child = bundle.getString("uid");

        if(state.equals("pet")) {
            setContentView(R.layout.activity_crudpet);

            name = (EditText)findViewById(R.id.txtPetNameEdit);
            weight = (EditText)findViewById(R.id.txtPetWeightEdit);
            birth = (EditText)findViewById(R.id.txtPetBirthEdit);
            race = (EditText) findViewById(R.id.txtPetRaceEdit);
            genre = (EditText)findViewById(R.id.txtPetGenreEdit);
            restrictions = (EditText)findViewById(R.id.txtPetRestrictionEdit);
            selectedPetName = (TextView)findViewById(R.id.pageTitle);

            name.setText(bundle.getString("petname"));
            weight.setText(bundle.getString("weight"));
            birth.setText(bundle.getString("birth"));
            selectedPetName.setText(bundle.getString("petname"));
            race.setText(bundle.getString("race"));
            genre.setText(bundle.getString("genre"));
            restrictions.setText(bundle.getString("restrictions"));
        }
        else if(state.equals("input"))
        {
            setContentView(R.layout.activity_crudinput);

            name = (EditText)findViewById(R.id.txtInputNameEdit);
            qnt = (EditText)findViewById(R.id.txtInputQntEdit);
            desc = (EditText)findViewById(R.id.txtInputDescEdit);
            selectedPetName = (TextView)findViewById(R.id.pageTitle);

            selectedPetName.setText(bundle.getString("name"));
            name.setText(bundle.getString("name"));
            qnt.setText(bundle.getString("qnt"));
            desc.setText(bundle.getString("description"));
        }
        //Passar outros itens;
        Toast.makeText(this, bundle.getString("petname") , Toast.LENGTH_LONG).show();

        initFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_save) {
            if(state.equals("pet")) {
                editPet(name.getText(), weight.getText(), birth.getText(), restrictions.getText(), race.getText(), genre.getText());
            }
            if(state.equals("input"))
            {
                editInput(name.getText(), qnt.getText(), desc.getText());
            }
            return true;
        }
        if (id == R.id.menu_delete)
        {
            AlertDialog al = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alerta")
                    .setMessage("Você tem certeza que quer deletar ?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePet();
                        }

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePet() {
        mDBReference.child(child).removeValue();
        finish();
        //Toast.makeText(CRUDPetActivity.this, "Excluído com sucesso", Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(CRUDPetActivity.this, HomeActivity.class));
    }

    private void editPet(Editable name, Editable weight, Editable date, Editable restriction ,Editable race ,Editable genre) {

        if(validate(name, weight, date)) {
            //Pet pet = new Pet(randomUUID().toString(), name.toString(), weight.toString(), date.toString());
            mDBReference.child(child).child("name").setValue(name.toString());
            mDBReference.child(child).child("weight").setValue(weight.toString());
            mDBReference.child(child).child("birthDate").setValue(date.toString());
            mDBReference.child(child).child("healthRestrictions").setValue(restriction.toString());
            mDBReference.child(child).child("race").setValue(race.toString());
            mDBReference.child(child).child("genre").setValue(genre.toString());
            Toast.makeText(CRUDPetActivity.this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(CRUDPetActivity.this, HomeActivity.class));
        }
    }

    private void editInput(Editable name, Editable qnt, Editable desc) {

            //Pet pet = new Pet(randomUUID().toString(), name.toString(), weight.toString(), date.toString());
            mDBReference.child(child).child("name").setValue(name.toString());
            mDBReference.child(child).child("weight").setValue(qnt.toString());
            mDBReference.child(child).child("birthDate").setValue(desc.toString());
            Toast.makeText(CRUDPetActivity.this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(CRUDPetActivity.this, HomeActivity.class));
        }

    private boolean validate(Editable name, Editable weight, Editable birth) {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setTitle("Ops !");
        errorDialog.setCancelable(true);
        TextView msg = new TextView(this);

        try {
            //validate mandatory fields
            if (name.toString().isEmpty()) {
                msg.setText("Nome é obrigatório.");
                errorDialog.setView(msg);
                AlertDialog alert = errorDialog.create();
                alert.show();
                return false;
            }
            if (weight.toString().isEmpty()) {
                msg.setText("Peso é obrigatório.");
                errorDialog.setView(msg);
                AlertDialog alert = errorDialog.create();
                alert.show();
                return false;
            }
            if (birth.toString().isEmpty()) {
                msg.setText("Data de nascimento é obrigatório.");
                errorDialog.setView(msg);
                AlertDialog alert = errorDialog.create();
                alert.show();
                return false;
            }
            //validate data types
            //Verify max weight
            if (Integer.parseInt(weight.toString()) <= 0 || Integer.parseInt(weight.toString()) > 240) {
                msg.setText("Peso inválido.");
                errorDialog.setView(msg);
                AlertDialog alert = errorDialog.create();
                alert.show();
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            msg.setText("Algo inesperado aconteceu. \nTente novamente.");
            errorDialog.setView(msg);
            AlertDialog alert = errorDialog.create();
            alert.show();
            return false;
        }
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
        mFirebaseDB = FirebaseDatabase.getInstance();
        mDBReference = mFirebaseDB.getReference();
        //sync the database
        mDBReference.keepSynced(true);
    }
}
