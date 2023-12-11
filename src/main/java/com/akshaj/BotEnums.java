package com.akshaj;

public class BotEnums {
    public enum KeyboardType{
        MAIN_KEYBOARD,CANCEL_KEYBOARD;
    }
    public enum UserState {
        SEARCH_STATE("searchState"),START_STATE("startState"),RECENT_STATE("recentState"),NONE_STATE("NONE_make_it_unique_asbhdbashdb_dsad"), BOOK_INFO_STATE("bookInfoState");

        private final String state;

        UserState(String state) {
            this.state=state;
        }

        @Override
        public String toString() {
            return this.state;
        }
    }

}

