package rpl1pnp.fikri.footballapps.ui.favorite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_favorite.*
import rpl1pnp.fikri.footballapps.R
import rpl1pnp.fikri.footballapps.ui.favorite.viewpager.SectionsPagerAdapter

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        view_pager_favorite.adapter = sectionsPagerAdapter
        tabs_favorite.setupWithViewPager(view_pager_favorite)
    }
}