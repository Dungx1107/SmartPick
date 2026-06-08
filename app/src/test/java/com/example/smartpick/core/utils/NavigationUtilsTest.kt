package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.navigation.Routes
import org.junit.Assert.*
import org.junit.Test

class NavigationUtilsTest : BaseUnitTest() {

    @Test
    fun `shouldShowBottomBar - Null route - Returns false`() {
        assertFalse(NavigationUtils.shouldShowBottomBar(null))
    }

    @Test
    fun `shouldShowBottomBar - Show Bottom Bar Routes`() {
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.Home.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.Feed.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.Notifications.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.Saved.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.Profile.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.ReviewHub.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.SellerDashboard.route))
        assertTrue(NavigationUtils.shouldShowBottomBar(Routes.LikedPosts.route))
    }

    @Test
    fun `shouldShowBottomBar - Hide Bottom Bar Static Routes`() {
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.Login.route))
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.SignUp.route))
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.EditProfile.route))
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.CreatePost.route))
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.Settings.route))
        assertFalse(NavigationUtils.shouldShowBottomBar(Routes.Cart.route))
    }

    @Test
    fun `shouldShowBottomBar - Hide Bottom Bar Checkout Variations`() {
        // Base Route
        assertFalse(NavigationUtils.shouldShowBottomBar("checkout"))
        // Route with params
        assertFalse(NavigationUtils.shouldShowBottomBar("checkout?productId=123&quantity=1"))
        assertFalse(NavigationUtils.shouldShowBottomBar("checkout?cartItemIds=item1,item2"))
    }

    @Test
    fun `shouldShowBottomBar - Hide Bottom Bar Dynamic Routes`() {
        // Product Detail
        assertFalse(NavigationUtils.shouldShowBottomBar("product_detail/prod_123"))
        // Post Detail
        assertFalse(NavigationUtils.shouldShowBottomBar("post_detail/post_123"))
        assertFalse(NavigationUtils.shouldShowBottomBar("post_detail/post_123?commentId=c_456"))
        // Edit Post
        assertFalse(NavigationUtils.shouldShowBottomBar("edit_post/post_123"))
        // Write Review
        assertFalse(NavigationUtils.shouldShowBottomBar("write_review/prod_123/order_456"))
        // Comments
        assertFalse(NavigationUtils.shouldShowBottomBar("comments/post_123/owner_456"))
        // Comments notification
        assertFalse(NavigationUtils.shouldShowBottomBar("comments_notification/post_123?commentId=c_456"))
    }
}
