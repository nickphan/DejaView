package com.deja11.dejaphoto;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by thefr on 5/14/2017.
 */

public class ControllerTest {
    Controller controller;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void TestControllerInitialize(){
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        assertTrue(controller.getCurrentWallpaper()!= null);
    }

    @Test
    public void TestNextPhoto(){
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        assertEquals(1, controller.getCache().size());
        for(int i = 0; i < 9; i++){
            Photo photo = controller.getNextPhoto();
            controller.setWallpaper(photo);
        }
        assertEquals(9, controller.getCache().size());
    }

    @Test
    public void TestPreviousPhoto(){
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        Photo firstPhoto = controller.getCurrentWallpaper();

        for(int i = 0; i < 10; i++) {
            Photo previousPhoto = controller.getCurrentWallpaper();
            Photo nextPhoto = controller.getNextPhoto();
            controller.setWallpaper(nextPhoto);
            assertEquals(previousPhoto, controller.getPreviousPhoto());
        }
    }

    @Test
    public void TestKarma(){
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        Photo photo = controller.getCurrentWallpaper();
        for(int i = 0; i < 10; i++){
            if(!controller.karmaPhoto()){
                assertTrue(photo.isKarma());
            }
            photo = controller.getNextPhoto();
            controller.setWallpaper(photo);
        }
    }

    @Test
    public void TestRelease(){
        /*FILL CACHE*/
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        for(int i = 0; i < 10; i++){
            Photo photo = controller.getNextPhoto();
            controller.setWallpaper(photo);
        }

        /*currPhoto is at index 5*/
        for(int i = 0; i < 4; i++){
            Photo photo = controller.getPreviousPhoto();
            controller.setWallpaper(photo);
        }
        Photo curr = controller.getCurrentWallpaper();
        controller.releasePhoto();

        assertFalse(curr.equals(controller.getCurrentWallpaper()));
        /*Move controller to beginning of cache*/
        for(int i = 0; i < 5; i++){
            Photo photo = controller.getPreviousPhoto();
            controller.setWallpaper(photo);
        }
        for(int i = 0; i < 10; i++){
            Photo photo = controller.getCurrentWallpaper();
            assertFalse(curr.equals(photo));
            Photo nextPhoto = controller.getNextPhoto();
            controller.setWallpaper(nextPhoto);
        }
    }

    @Test
    public void TestSetWallpaper(){
        controller = new Controller(mainActivityActivityTestRule.getActivity());
        for(int i = 0; i < 9; i++){
            Photo photo = controller.getNextPhoto();
            controller.setWallpaper(photo);
        }
        assertEquals(9, controller.getCache().size());

        Photo photo = controller.getPreviousPhoto();
        controller.setWallpaper(photo);
        assertEquals(10, controller.getCache().size());

        for(int i = 0; i < 10; i++){
            photo = controller.getNextPhoto();
            controller.setWallpaper(photo);
        }
        assertEquals(10, controller.getCache().size());
    }
}
