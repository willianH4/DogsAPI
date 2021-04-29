package com.willianhdz.listdogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.willianhdz.listdogs.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImage = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svDogs.setOnQueryTextListener(this)
        initRecyclerview()
    }

    private fun initRecyclerview() {
        adapter = DogAdapter(dogImage)
        binding.rvDogs.layoutManager = LinearLayoutManager(this)
        binding.rvDogs.adapter = adapter
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
                .baseUrl("https://dog.ceo/api/breed/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun searchByName(query: String){
        CoroutineScope(Dispatchers.IO).launch {
            val call:Response<DogResponse> = getRetrofit().create(APIService::class.java).getDog("$query/images")
            val pupies: DogResponse ? = call.body()
            runOnUiThread {
                if (call.isSuccessful){
                    // Show recyclerview
                    val images: List<String> = pupies ?. images ?: emptyList()
                    dogImage.clear()
                    dogImage.addAll(images)
                    adapter.notifyDataSetChanged()
                }else{
                //Mostrar mensaje de error en caso algo falle
                    showError()
                }
                // funcion que oculta el teclado al lanzar la busqueda
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError() {
        Toast.makeText(this, "Error de conexion, intentalo mas tarde!", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            searchByName(query.toLowerCase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}