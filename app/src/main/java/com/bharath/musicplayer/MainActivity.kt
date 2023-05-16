package com.bharath.musicplayer

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bharath.musicplayer.adapter.SongAdapter
import com.bharath.musicplayer.model.Song
import com.bharath.musicplayer.permission.CheckPermission
import com.bharath.musicplayer.viewmodels.SongViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),SongAdapter.onClicked{
    lateinit var permissionLauncher:CheckPermission
    lateinit var songViewModel: SongViewModel
    lateinit var songAdapter: SongAdapter
    lateinit var recyclerView: RecyclerView
    private var playView:ConstraintLayout?=null
    lateinit var exoPlayer: ExoPlayer
    private val list:MutableList<Song> = ArrayList()

    //Views of Playing View
    private lateinit var PValbumArt:ImageView
    private lateinit var PVsongName:TextView
    private lateinit var PVsongArtist:TextView
    private lateinit var PVPlayButton:ImageView
    private lateinit var SkipNextButton:ImageView
    private lateinit var SkipPrevious:ImageView
    private lateinit var backbutton:ImageView
    private lateinit var seekBar: SeekBar



    // Views in MiniPlayer

    private lateinit var MN_SongName:TextView
    private lateinit var MN_AlbumArt:ImageView
    private lateinit var MN_playBtn: ImageView
    private lateinit var MN_COnstraint:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        exoPlayer=ExoPlayer.Builder(this).build()
        songViewModel=ViewModelProvider(this)[SongViewModel::class.java]
        playView=findViewById(R.id.playingView)
        PValbumArt=findViewById(R.id.PV_AlbumArt)
        PVsongName=findViewById(R.id.SongName_PlayView)
        PVsongArtist=findViewById(R.id.SongArtist_PlayView)
        backbutton=findViewById(R.id.BackButton)
        PVPlayButton=findViewById(R.id.PauseButton_Play)
        SkipNextButton=findViewById(R.id.SkipNext)
        SkipPrevious=findViewById(R.id.SkipPrevious)
        seekBar=findViewById(R.id.SeekBar)

        MN_AlbumArt=findViewById(R.id.MiniAlbumArt)
        MN_SongName=findViewById(R.id.MiniSongName)
        MN_playBtn=findViewById(R.id.MiniPlayBtn)
        MN_COnstraint=findViewById(R.id.MiniPlayerCo)
        MN_COnstraint.visibility= View.GONE





        if (exoPlayer.isPlaying){
            setUiPV()
        }


        permissionLauncher=CheckPermission(this@MainActivity)
        permissionLauncher.checkPermission()
        recyclerView=findViewById(R.id.RecyclerView_Home)
        CoroutineScope(Dispatchers.Main).apply {
            CoroutineScope(Dispatchers.Default).apply {
                list.addAll(songViewModel.getAllAudio(this@MainActivity))
                songAdapter= SongAdapter(this@MainActivity,this@MainActivity,list)
            }
            recyclerView.layoutManager=LinearLayoutManager(this@MainActivity)
            recyclerView.adapter=songAdapter

        }

    }


    fun setUiPV(){
        setImages()
        setTexts()
        setControls()
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            setSeekBar()

        }

    }

    private fun setControls() {
        backbutton.setOnClickListener {
            playView!!.visibility=View.GONE
        }
        PVPlayButton.setOnClickListener {
            if(exoPlayer.isPlaying){
                PVPlayButton.setImageResource(R.drawable.baseline_pause_24)
                exoPlayer.pause()
                PVPlayButton.setImageResource(R.drawable.baseline_play_arrow_24)


            }
            else{
                exoPlayer.play()


                PVPlayButton.setImageResource(R.drawable.baseline_pause_24)
            }
        }
        SkipPrevious.setOnClickListener {
            if (exoPlayer.hasPreviousMediaItem()){
                exoPlayer.seekToPrevious()
                setTexts()
                setImages()
            }

        }
        SkipNextButton.setOnClickListener {
            if (exoPlayer.hasNextMediaItem()){
                exoPlayer.seekToNext()
                setTexts()
                setImages()
            }
        }
        MN_playBtn.setOnClickListener {
            if(exoPlayer.isPlaying){
                exoPlayer.pause()
                MN_playBtn.setImageResource(R.drawable.baseline_play_arrow_24)


            }
            else{
                exoPlayer.play()


                MN_playBtn.setImageResource(R.drawable.baseline_pause_24)
            }
        }
        updtExPlayer()
        MN_COnstraint.setOnClickListener {
            playView!!.visibility =View.VISIBLE
        }

    }
    fun updtExPlayer(){
        exoPlayer.addListener(object :Player.Listener{
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                setUiPV()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                setUiPV()
            }
        })
    }


    private fun setTexts() {
        PVsongName.text=exoPlayer.currentMediaItem!!.mediaMetadata.title
        PVsongArtist.text=exoPlayer.currentMediaItem!!.mediaMetadata.artist
        MN_SongName.text=exoPlayer.currentMediaItem!!.mediaMetadata.title
        PVsongArtist.isSelected=true
        PVsongName.isSelected=true
        MN_SongName.isSelected=true

    }

    private fun setImages() {
        Glide.with(this@MainActivity)
            .load(exoPlayer.currentMediaItem!!.mediaMetadata.artworkUri)
            .placeholder(R.drawable.baseline_music_note_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(PValbumArt)
        Glide.with(this@MainActivity)
            .load(exoPlayer.currentMediaItem!!.mediaMetadata.artworkUri)
            .placeholder(R.drawable.baseline_music_note_24)
            .override(250,250)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(MN_AlbumArt)

    }
    private fun setSeekBar(){
        //setting seekbar
        seekBar.max=exoPlayer.duration.toInt()
        seekBar.progress=exoPlayer.currentPosition.toInt()
        getCurrentSeekBarPosition()

        seekBar.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    exoPlayer.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        }
        )
    }
    private fun getCurrentSeekBarPosition(){
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            if (exoPlayer.isPlaying){
                seekBar.progress=exoPlayer.currentPosition.toInt()


            }
            getCurrentSeekBarPosition()
        }

    }

    override fun onClick(position: Int) {
        playExoPlayer(position)
       CoroutineScope(Dispatchers.Main).launch {
        MN_COnstraint.visibility=View.VISIBLE

       }
//        setUiPV()

    }

    private fun playExoPlayer(position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                exoPlayer.seekTo(position, 0)

            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    exoPlayer.setMediaItems(songViewModel.getMediaMetaItems(list))
                    exoPlayer.seekTo(position, 0)
                }

            }
            exoPlayer.prepare()
            exoPlayer.play()
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                setUiPV()
            }
        }
    }

    override fun onDestroy() {
        if (exoPlayer.isPlaying)exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }



}