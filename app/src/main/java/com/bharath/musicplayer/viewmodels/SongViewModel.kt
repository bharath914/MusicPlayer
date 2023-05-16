package com.bharath.musicplayer.viewmodels

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bharath.musicplayer.model.Song

class SongViewModel:ViewModel() {


    fun getAllAudio(context:Context):MutableList<Song>{

        val list:MutableList<Song> =ArrayList()
        val mediaUri:Uri
        mediaUri=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        }else{
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val projection= arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM_ID

        )
        val selection=MediaStore.Audio.AudioColumns.IS_MUSIC
        val sortOrder=MediaStore.Audio.AudioColumns.DATE_MODIFIED +" DESC"
        context.contentResolver.query(mediaUri,projection,selection,null,sortOrder).use {cursor->
            val id_C=cursor!!.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val title_C=cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val artist_C=cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val albumId_C=cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)

            while (cursor.moveToNext()){
                val id=cursor.getLong(id_C)
                val title=cursor.getString(title_C)
                val artist=cursor.getString(artist_C)
                val albumId=cursor.getLong(albumId_C)

                val songUri=ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)
                //content://media/external/audio/albumart
                val albumartWorkUri=ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumId)

                val song=Song(songUri,title,artist,albumartWorkUri)
                list.add(song)
                Log.d("songNAme",song.songName)
            }
            cursor.close()
        }
        return list

    }


    fun getMediaMetaItems(list:MutableList<Song>):MutableList<MediaItem>{
        val metalist:MutableList<MediaItem> = ArrayList()
        for (song in list){
            val metadata=MediaItem.Builder()
                .setUri(song.songurl)
                .setMediaMetadata(getMediaMetaData(song))
                .build()
            metalist.add(metadata)
        }
        return metalist


    }

    private fun getMediaMetaData(song: Song): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(song.songName)
            .setArtist(song.songArtist)
            .setArtworkUri(song.albumartUri)
            .build()
    }



}