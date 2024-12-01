package com.example.refresh.Database;

import com.example.refresh.Database.Tables.ColumnEnum;

public class TableCols {
    public enum Tables implements ColumnEnum {
        USERS("Users"),
        NOTIFICATION_TEMPLATES("NotificationTemplates"),
        NOTIFICATION_INSTANCES("NotificationInstances");

        private final String name;

        Tables(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    public enum UserCols implements ColumnEnum {
        NAME("Name"),
        EMAIL("Email"),
        PHONE("Phone"),
        PWD("Password"),
        PWD_CONF("PasswordConfirmation");

        private final String name;

        UserCols(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum NotificationTemplateCols implements ColumnEnum {
        TEMPLATE_ID("TemplateID"),
        TITLE("Title"),
        MESSAGE("Message"),
        ICON("Icon"),
        COLOR("Color");

        private final String name;

        NotificationTemplateCols(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum NotificationInstanceCols implements ColumnEnum {
        INSTANCE_ID("InstanceID"),
        TEMPLATE_ID("TemplateID"),
        TIME("Time");

        private final String name;

        NotificationInstanceCols(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
