package rpl1pnp.fikri.footballapps.ui.detailmatch

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_match.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.jetbrains.anko.design.snackbar
import rpl1pnp.fikri.footballapps.R
import rpl1pnp.fikri.footballapps.model.Event
import rpl1pnp.fikri.footballapps.model.Team
import rpl1pnp.fikri.footballapps.network.ApiRepository
import rpl1pnp.fikri.footballapps.util.CoroutineContextProvider
import rpl1pnp.fikri.footballapps.util.invisible
import rpl1pnp.fikri.footballapps.util.visible
import rpl1pnp.fikri.footballapps.view.DetailMatchView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class DetailMatchActivity : AppCompatActivity(), DetailMatchView, CoroutineScope {
    private lateinit var presenter: DetailMatchPresenter
    private val coroutineContextProvider = CoroutineContextProvider()
    private lateinit var job: Job
    private var menuItem: Menu? = null
    private var isFavorite: Boolean = false
    private var eventId: String? = null
    private var events: List<Event>? = null
    private var homeTeamId: String? = null
    private var awayTeamId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_match)
        setSupportActionBar(toolbar_match_detail)
        supportActionBar?.title = getString(R.string.detail_match)
        job = Job()

        getDataPresenter()
        presenter.favoriteState(this, eventId)
        setFavorite()
    }

    private fun getDataPresenter() {
        intentExtra()

        val api = ApiRepository()
        val gson = Gson()
        presenter = DetailMatchPresenter(this, api, gson)
        presenter.getEvent(eventId)
        presenter.getLogo(homeTeamId, true)
        presenter.getLogo(awayTeamId, false)
    }

    private fun intentExtra() {
        eventId = intent.getStringExtra("EVENT_ID")
        homeTeamId = intent.getStringExtra("HOME_TEAM")
        awayTeamId = intent.getStringExtra("AWAY_TEAM")
    }

    override fun getLogoTeam(data: List<Team>, isHomeTeam: Boolean) {
        if (isHomeTeam) {
            Picasso.get().load(data.first().strTeamBadge).fit().into(image_home_badge)
        } else {
            Picasso.get().load(data.first().strTeamBadge).fit().into(image_away_badge)
        }
    }

    override fun addFavorite() {
        ly_detail_match.snackbar(getString(R.string.added_favorite))
            .setTextColor(ContextCompat.getColor(this, R.color.textColorSnackBar))
    }

    override fun removeFavorite() {
        ly_detail_match.snackbar(getString(R.string.remove_favorite))
            .setTextColor(ContextCompat.getColor(this, R.color.textColorSnackBar))
    }

    override fun favoriteState(state: Boolean) {
        isFavorite = state
    }

    override fun errorFavorite(message: CharSequence?) {
        message?.let { ly_detail_match.snackbar(it) }
    }

    override fun showLoading() {
        progressBar(true)
    }

    override fun hideLoading() {
        progressBar(false)
    }

    override fun showDetail(data: List<Event>) {
        events = data
        val df = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = df.parse(data.first().dateEvent + "\n" + data.first().time)
        df.timeZone = (TimeZone.getDefault())
        val formattedDate: String? = df.format(date!!)
        text_event_date.text = formattedDate.orEmpty()
        text_home_name.text = data.first().homeTeam.orEmpty()
        text_home_score.text = data.first().homeScore.orEmpty()
        text_home_goals.text = data.first().homeGoalDetail.orEmpty().replace(";", "\n")
        text_home_formation.text = data.first().homeFormation.orEmpty()
        text_home_red_cards.text = data.first().homeRedCard.orEmpty()
        text_home_yellow_cards.text = data.first().homeYellowCard.orEmpty()
        text_home_formation.text = data.first().homeFormation.orEmpty()

        text_away_name.text = data.first().awayTeam.orEmpty()
        text_away_score.text = data.first().awayScore.orEmpty()
        text_away_goals.text = data.first().awayGoalDetail.orEmpty()
        text_away_formation.text = data.first().awayFormation.orEmpty()
        text_away_red_cards.text = data.first().awayRedCard.orEmpty()
        text_home_yellow_cards.text = data.first().awayYellowCard.orEmpty()
        text_away_formation.text = data.first().awayFormation.orEmpty()

        if (data.first().homeScore == null && data.first().awayScore == null) {
            text_divide_score.text = resources.getString(R.string.vs)
        } else {
            text_divide_score.text = resources.getString(R.string.strip)
        }
    }

    private fun progressBar(isTrue: Boolean) {
        if (isTrue) {
            return progress_detail_match.visible()
        }
        return progress_detail_match.invisible()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_match_menu, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.btn_match_favorite -> {
                if (isFavorite) {
                    presenter.removeFromFavorite(this, eventId)
                    isFavorite = !isFavorite
                } else {
                    if (events.isNullOrEmpty()) ly_detail_match.snackbar(getString(R.string.favorite_failed))
                        .setTextColor(ContextCompat.getColor(this, R.color.textColorSnackBar))
                    else {
                        presenter.addToFavorite(this, events)
                        isFavorite = !isFavorite
                    }
                }

                setFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setFavorite() {
        if (isFavorite) {
            menuItem?.getItem(0)?.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_added_favorite)
        } else {
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_add_favorite)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + coroutineContextProvider.main

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
