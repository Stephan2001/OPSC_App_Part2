package com.example.timeflow_opsc_poe_part_2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private  lateinit var projectReference : DatabaseReference
    private  lateinit var rootNode : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/${CurrentUser.userID}")
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                UserProjects.projectsList.clear()
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2 != null && dc2.highPriority) {
                        UserProjects.projectsList.add(dc2.name)
                        Log.w("errrrr", dc2.name)
                    }
                }
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2 != null && !dc2.highPriority) {
                        UserProjects.projectsList.add(dc2.name)
                        Log.w("errrrr", dc2.name)
                    }
                }

            }
            override fun onCancelled(error: DatabaseError){

            }
        })

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null){
            replaceFragment(ScheduleFragment())
            navigationView.setCheckedItem(R.id.nav_schedule)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_schedule -> replaceFragment(ScheduleFragment())
            R.id.nav_projects -> replaceFragment(ProjectsFragment())
            R.id.nav_statistics -> replaceFragment(StatisticsFragment())
            R.id.nav_goals -> replaceFragment(GoalsFragment())
            R.id.nav_logout -> logoutDialog()//Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    fun logoutDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder

            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss();
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun logout(){
        auth = Firebase.auth
        auth.signOut()
        CurrentUser.userID = ""
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show()
    }

    private  fun  replaceFragment(fragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onBackPressed(){
        super.onBackPressed()
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

}

