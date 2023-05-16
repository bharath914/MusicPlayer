package com.bharath.musicplayer.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bharath.musicplayer.R
import com.bharath.musicplayer.model.Song
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class SongAdapter(
    val listener:SongAdapter.onClicked,
    val context:Context,
    val list:MutableList<Song> = ArrayList()
):RecyclerView.Adapter<SongAdapter.MyViewHolder>()

{
    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val artwork:ImageView=itemView.findViewById(R.id.ALbumArtWork)
        val songName:TextView=itemView.findViewById(R.id.SongName)
        val songArtist:TextView=itemView.findViewById(R.id.SongArtist)
        val cardView:CardView=itemView.findViewById(R.id.CardViewListItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
     return MyViewHolder(
         LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
     )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val currentItem=list[position]
        holder.apply {
            songName.text=currentItem.songName
            songArtist.text=currentItem.songArtist
        }
        Glide.with(context)
            .load(currentItem.albumartUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.baseline_music_note_24)
            .override(dptoPixel(),dptoPixel())
            .into(holder.artwork)
        holder.cardView.setOnClickListener {
            listener.onClick(holder.adapterPosition)
        }
    }
    private fun dptoPixel():Int{
        return (100*Resources.getSystem().displayMetrics.densityDpi).toInt()
    }
    interface onClicked{
        fun onClick(position: Int)
    }


}