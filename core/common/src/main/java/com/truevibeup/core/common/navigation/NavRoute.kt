package com.truevibeup.core.common.navigation

sealed class NavRoute(val route: String) {
    object Splash : NavRoute("splash")
    object SelectLanguage : NavRoute("select_language")
    object Welcome : NavRoute("welcome")
    object Register : NavRoute("register")
    object Login : NavRoute("login")
    object OnboardingFlow : NavRoute("onboarding")

    object Onboarding {
        object Step1 : NavRoute("onboarding/1")
        object Step2 : NavRoute("onboarding/2")
        object Step3 : NavRoute("onboarding/3")
        object Step4 : NavRoute("onboarding/4")
        object Step5 : NavRoute("onboarding/5")
        object Step6 : NavRoute("onboarding/6")
        object Step7 : NavRoute("onboarding/7")
    }

    object Main : NavRoute("main")
    
    object ChatRoom : NavRoute("chat/{conversationId}") {
        fun createRoute(id: Long) = "chat/$id"
    }
    
    object UserProfile : NavRoute("user/{uuid}") {
        fun createRoute(uuid: String) = "user/$uuid"
    }
    
    object PostDetail : NavRoute("post/{postId}?scrollToComments={scrollToComments}") {
        fun createRoute(id: Long, scrollToComments: Boolean = false) =
            "post/$id?scrollToComments=$scrollToComments"
    }
    
    object EditProfile : NavRoute("profile/edit")

    object Settings : NavRoute("settings")

    object Follow : NavRoute("follow/{userId}/{initialTab}") {
        fun createRoute(userId: String, initialTab: Int = 0) = "follow/$userId/$initialTab"
    }
}
