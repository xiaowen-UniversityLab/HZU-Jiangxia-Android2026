package com.example.moviecollection.bin;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieDao mMovieDao;
    private LiveData<List<Results>> allMovieLive;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase movieDatabase = MovieDatabase.getDatabase(application);
        mMovieDao = movieDatabase.getResultsDao();
        allMovieLive = mMovieDao.getAllResultLive();

    }

    public LiveData<List<Results>> getAllMovieLive() {
        return allMovieLive;
    }

    public void insertMovie(Results... results) {
        new InsertAsyncTask(mMovieDao).execute(results);
    }

    public void deleteMovie(Long movieId) {
        new DeleteByIdAsyncTask(mMovieDao).execute(movieId);
    }


    static class InsertAsyncTask extends AsyncTask<Results, Void, Void> {
        private MovieDao mMovieDao;

        public InsertAsyncTask(MovieDao movieDao) {
            mMovieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Results... results) {
            mMovieDao.insertMovie(results);
            return null;
        }
    }

    static class DeleteByIdAsyncTask extends AsyncTask<Long, Void, Void> {
        private MovieDao mMovieDao;

        public DeleteByIdAsyncTask(MovieDao movieDao) {
            mMovieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Long... movieId) {
            if (movieId != null && movieId.length > 0) {
                mMovieDao.deleteMovieById(movieId[0]);
            }
            return null;
        }
    }
}
