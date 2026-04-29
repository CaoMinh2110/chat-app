package com.truevibeup.core.common

object AppConstants {
    const val MAX_COMMENT_LEVEL = 2
    const val MAX_PROFILE_PHOTO = 2
    const val MIN_AGE = 18
    const val MAX_AGE = 80
}

data class OptionItem(
    val value: String,
    val translationKey: Int
)

object Options {
    val GENDER_OPTIONS = listOf(
        OptionItem("Male", R.string.gender_male),
        OptionItem("Female", R.string.gender_female),
        OptionItem("Other", R.string.option_other),
    )

    val HOBBIES_OPTIONS = listOf(
        OptionItem("Traveling", R.string.hobbies_traveling),
        OptionItem("Football", R.string.hobbies_football),
        OptionItem("Watching TV", R.string.hobbies_watching_tv),
        OptionItem("Baseball", R.string.hobbies_baseball),
        OptionItem("Music", R.string.hobbies_music),
        OptionItem("Cars", R.string.hobbies_cars),
        OptionItem("Basketball", R.string.hobbies_basketball),
        OptionItem("Cooking", R.string.hobbies_cooking),
        OptionItem("Pets", R.string.hobbies_pets),
        OptionItem("Working out", R.string.hobbies_working_out),
        OptionItem("Computer games", R.string.hobbies_computer_games),
        OptionItem("Dancing", R.string.hobbies_dancing),
        OptionItem("Arts", R.string.hobbies_arts),
        OptionItem("Gardening", R.string.hobbies_gardening),
        OptionItem("Fishing", R.string.hobbies_fishing),
        OptionItem("Hunting", R.string.hobbies_hunting),
        OptionItem("Hockey", R.string.hobbies_hockey),
        OptionItem("Reading", R.string.hobbies_reading),
        OptionItem("Boxing", R.string.hobbies_boxing),
        OptionItem("Fashion", R.string.hobbies_fashion),
        OptionItem("Nature", R.string.hobbies_nature),
        OptionItem("Soccer", R.string.hobbies_soccer),
        OptionItem("Biking", R.string.hobbies_biking),
        OptionItem("Tennis", R.string.hobbies_tennis),
        OptionItem("Astrology", R.string.hobbies_astrology),
        OptionItem("Golf", R.string.hobbies_golf),
        OptionItem("Photography", R.string.hobbies_photography),
        OptionItem("Fitness", R.string.hobbies_fitness),
        OptionItem("Yoga", R.string.hobbies_yoga),
        OptionItem("Volunteering", R.string.hobbies_volunteering),
        OptionItem("Shopping", R.string.hobbies_shopping),
        OptionItem("Other", R.string.option_other)
    )

    val TRAITS_OPTIONS = listOf(
        OptionItem("honest", R.string.traits_honest),
        OptionItem("cheerful", R.string.traits_cheerful),
        OptionItem("moody", R.string.traits_moody),
        OptionItem("self-confident", R.string.traits_self_confident),
        OptionItem("optimistic", R.string.traits_optimistic),
        OptionItem("calm", R.string.traits_calm),
        OptionItem("grateful", R.string.traits_grateful),
        OptionItem("thoughtful", R.string.traits_thoughtful),
        OptionItem("kind", R.string.traits_kind),
        OptionItem("humorous", R.string.traits_humorous),
        OptionItem("fun", R.string.traits_fun),
        OptionItem("generous", R.string.traits_generous),
        OptionItem("shy", R.string.traits_shy),
        OptionItem("curious", R.string.traits_curious),
        OptionItem("caring", R.string.traits_caring),
        OptionItem("likeable", R.string.traits_likeable),
        OptionItem("loyal", R.string.traits_loyal),
        OptionItem("quiet", R.string.traits_quiet),
        OptionItem("serious", R.string.traits_serious)
    )

    val LOOKING_FOR_OPTIONS = listOf(
        OptionItem("chatting", R.string.looking_chatting),
        OptionItem("finding a friend", R.string.looking_finding_friend),
        OptionItem("having fun", R.string.looking_having_fun),
        OptionItem("get attention", R.string.looking_get_attention),
        OptionItem("I am bored", R.string.looking_bored),
        OptionItem("other", R.string.option_other)
    )

    val MOVIES_OPTIONS = listOf(
        OptionItem("Documentary", R.string.movies_documentary),
        OptionItem("Animation", R.string.movies_animation),
        OptionItem("Comedy", R.string.movies_comedy),
        OptionItem("Adventure", R.string.movies_adventure),
        OptionItem("Horror", R.string.movies_horror),
        OptionItem("Action", R.string.movies_action),
        OptionItem("Thriller", R.string.movies_thriller),
        OptionItem("Drama", R.string.movies_drama),
        OptionItem("Romcom", R.string.movies_romcom),
        OptionItem("Western", R.string.movies_western),
        OptionItem("Science fiction", R.string.movies_science_fiction),
        OptionItem("Crime film", R.string.movies_crime_film),
        OptionItem("Historical drama", R.string.movies_historical_drama),
        OptionItem("Fantasy", R.string.movies_fantasy),
        OptionItem("Don't like movies", R.string.movies_dont_like)
    )

    val MUSIC_OPTIONS = listOf(
        OptionItem("Rock", R.string.music_rock),
        OptionItem("Pop", R.string.music_pop),
        OptionItem("Electronic", R.string.music_electronic),
        OptionItem("Classical music", R.string.music_classical),
        OptionItem("Jazz", R.string.music_jazz),
        OptionItem("Hip-hop", R.string.music_hip_hop),
        OptionItem("Melomaniac", R.string.music_melomaniac),
        OptionItem("Country", R.string.music_country),
        OptionItem("Folk", R.string.music_folk),
        OptionItem("Don't like music", R.string.music_dont_like)
    )

    val PERSONALITIES_OPTIONS = listOf(
        OptionItem("Film aficionado", R.string.personalities_film_aficionado),
        OptionItem("Music addict", R.string.personalities_music_addict),
        OptionItem("Fitness freak", R.string.personalities_fitness_freak),
        OptionItem("Career chaser", R.string.personalities_career_chaser),
        OptionItem("Homebody", R.string.personalities_homebody),
        OptionItem("Travel enthusiast", R.string.personalities_travel_enthusiast),
        OptionItem("Nature lover", R.string.personalities_nature_lover),
        OptionItem("Other", R.string.option_other)
    )
}
