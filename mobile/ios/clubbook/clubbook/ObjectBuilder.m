    //
//  ObjectBuilder.m
//  BrowseMeetup
//
//  Created by Andrew on 10/1/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import "ObjectBuilder.h"
#import "User.h"
#import "Place.h"
#import "Conversation.h"
#import "WorkingHour.h"
#import "Config.h"
#import "NewsData.h"
#import "Event.h"

@implementation ObjectBuilder

+ (User *)checkinFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    NSDictionary *userJson = [parsedObject objectForKey:@"user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
    
    return user;

}

+ (User *)signupFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSDictionary *userJson = [result objectForKey:@"user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
    
    return user;
}


+ (User *)signinFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSDictionary *userJson = [result objectForKey:@"user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
    
    return user;
}

+ (User *)fbLoginFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSDictionary *userJson = [result objectForKey:@"user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
          
    return user;

}

+ (NSArray *)placesFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *places = [[NSMutableArray alloc] init];
    
    NSArray *venues = [parsedObject objectForKey:@"venues"];
    
    for (NSDictionary *placeDic in venues) {
        Place *place = [[Place alloc] init];
        
        place.id  = [placeDic objectForKey:@"id"];
        place.title = [placeDic objectForKey:@"club_name"];
        place.phone = [placeDic objectForKey:@"club_phone"];
        place.address = [placeDic objectForKey:@"club_address"];
        place.email = [placeDic objectForKey:@"club_email"];
        place.site = [placeDic objectForKey:@"club_site"];
        place.avatar = [placeDic objectForKey:@"club_logo"];
        place.info = [placeDic objectForKey:@"club_info"];
        place.capacity = [[placeDic objectForKey:@"club_capacity"] intValue];
        place.ageRestriction = [placeDic objectForKey:@"club_age_restriction"];
        place.dressCode = [placeDic objectForKey:@"club_dress_code"];
        place.distance = [[placeDic objectForKey:@"distance"] doubleValue] * 1000; // convert to meters;
        
        place.lon = [[placeDic objectForKey:@"club_loc"] objectForKey:@"lon"];
        place.lat = [[placeDic objectForKey:@"club_loc"] objectForKey:@"lat"];
        place.countOfUsers = [[placeDic objectForKey:@"active_checkins"] intValue];
        place.friendsCount = [[placeDic objectForKey:@"active_friends_checkins"] intValue];
        place.category = [placeDic objectForKey:@"category"];
        place.isFavorite = NO;
        NSNumber *isFavorite = [placeDic objectForKey:@"is_favorite"];
        if ([isFavorite intValue] > 0) {
            place.isFavorite = YES;
        }
        
        //place.friendsCount = [[parsedObject objectForKey:@"friends_count"] intValue];
        
        NSMutableArray *photos = [[NSMutableArray alloc] init];
        
        NSArray *club_photos = [placeDic objectForKey:@"club_photos"];
        
        for (NSString *club_photo in club_photos) {
            [photos addObject:club_photo];
        }
        
        place.photos = photos;
        
        // get working hours
        NSMutableArray *clubWorkingHours = [[NSMutableArray alloc] init];
        NSArray *clubWorkingHoursJson = [placeDic objectForKey:@"club_working_hours"];
        for (NSDictionary *clubWorkingHourJson in clubWorkingHoursJson) {
            WorkingHour *workingHour = [[WorkingHour alloc] init];
            workingHour.day = [[clubWorkingHourJson objectForKey:@"day"] intValue];
            workingHour.startTime = [clubWorkingHourJson objectForKey:@"start_time"];
            workingHour.endTime = [clubWorkingHourJson objectForKey:@"end_time"];
            workingHour.status = [clubWorkingHourJson objectForKey:@"status"];
            [clubWorkingHours addObject:workingHour];
        }
        place.workingHours = clubWorkingHours;
        
        // get today working hours
        NSDictionary *clubWorkingHoursTodayJson = [placeDic objectForKey:@"club_today_working_hours"];
        WorkingHour *workingHour = [[WorkingHour alloc] init];
        
        if ([clubWorkingHoursTodayJson valueForKey:@"day"] != nil && [clubWorkingHoursTodayJson valueForKey:@"day"] != [NSNull null]) {
            workingHour.day = [[clubWorkingHoursTodayJson objectForKey:@"day"] intValue];
            workingHour.startTime = [clubWorkingHoursTodayJson objectForKey:@"start_time"];
            workingHour.endTime = [clubWorkingHoursTodayJson objectForKey:@"end_time"];
            workingHour.status = [clubWorkingHoursTodayJson objectForKey:@"status"];
            place.todayWorkingHours = workingHour;
        }
        
        [places addObject:place];
    }
    
    return places;
}

+ (NSArray *)eventsFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *events = [[NSMutableArray alloc] init];
    
    NSArray *elements = [parsedObject objectForKey:@"events"];
    
    for (NSDictionary *eventDic in elements) {
        Event *event = [[Event alloc] init];
        
        NSString* dateStr = [eventDic objectForKey:@"start_time_formatted"];
        // Convert string to date object
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
        event.startTime = [dateFormat dateFromString:dateStr];
       
        dateStr = [eventDic objectForKey:@"end_time_formatted"];
        event.endTime = [dateFormat dateFromString:dateStr];
        
        event.title = [eventDic objectForKey:@"title"];
        event.share = [eventDic objectForKey:@"share"];
        event.buyTickets = [eventDic objectForKey:@"buy_tickets"];
        event.eventDescription = [eventDic objectForKey:@"description"];
        event.photos = [eventDic objectForKey:@"photo"];
        event.locationName = [eventDic objectForKey:@"loc_name"];
        event.location = [eventDic objectForKey:@"loc"];
        event.address = [eventDic objectForKey:@"address"];
        event.distance = [[eventDic objectForKey:@"distance"] doubleValue] * 1000;
        
        NSDictionary* venue = [eventDic objectForKey:@"club"];
        if (venue == nil) {
            venue = [eventDic objectForKey:@"festival"];
        }
        
        event.place = nil;
        if (venue != nil) {
            event.place = [self getPlace:venue];
        }
        
        NSDictionary* dj = [eventDic objectForKey:@"dj"];
        event.dj = nil;
        if (dj != nil) {
            event.dj = [self getDJAndBand:dj];
        }
        
        
        [events addObject:event];
        }
    
    return events;
}

+ (NSArray*) djsAndBandsFromJSON:(NSData *)objectNotation error:(NSError **)error {
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *djs = [[NSMutableArray alloc] init];
    
    NSArray *elements = [parsedObject objectForKey:@"events"];
    
    for (NSDictionary *djsDic in elements) {
        DJ* dj = [self getDJAndBand:djsDic];
        [djs addObject:dj];
    }
    return djs;
}

+  (DJ*) getDJAndBand:(NSDictionary*) dict {
    DJ *dj = [[DJ alloc] init];
    
    dj.djId = [dict objectForKey:@"_id"];
    dj.avatar = [dict objectForKey:@"logo"];
    dj.name = [dict objectForKey:@"name"];
    dj.email = [dict objectForKey:@"email"];
    dj.phone = [dict objectForKey:@"phone"];
    dj.music = [dict objectForKey:@"music"];
    dj.info = [dict objectForKey:@"info"];
    dj.website = [dict objectForKey:@"site"];
    
    NSMutableArray *photos = [[NSMutableArray alloc] init];
    
    NSArray *dj_photos = [dict objectForKey:@"photos"];
    
    for (NSString *dj_photo in dj_photos) {
        [photos addObject:dj_photo];
    }
    
    dj.photos = photos;
    
    return dj;
}

+ (FriendsResult *)friendsJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSArray *friendsJson = [result objectForKey:@"friends"];
    
    NSMutableArray *friends = [[NSMutableArray alloc] init];
    
    for (NSDictionary *friendDic in friendsJson) {
        User *friend = [self getUserBase:parsedObject userJson:friendDic];
        
        [friends addObject:friend];
    }
    
    FriendsResult *friendResult = [[FriendsResult alloc] init];
    friendResult.friends = friends;
    if ([result valueForKey:@"pending_count"] != nil && [result valueForKey:@"pending_count"] != [NSNull null]) {
        friendResult.countOfPendings = [[result objectForKey:@"pending_count"] intValue];
    }
    
    if ([result valueForKey:@"friends_count"] != nil && [result valueForKey:@"friends_count"] != [NSNull null]) {
        friendResult.countOfFriends = [[result objectForKey:@"friends_count"] intValue];
    }
    
    return friendResult;
}

+ (NSArray *)usersJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSArray *usersJson = [result objectForKey:@"users"];
    
    NSMutableArray *users = [[NSMutableArray alloc] init];
    
    for (NSDictionary *userDic in usersJson) {
        User *user = [self getUserBase:parsedObject userJson:userDic];
        
        [users addObject:user];
    }
    
    return users;
}

+ (NSArray *)placeUsersFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *users = [[NSMutableArray alloc] init];
    NSArray *usersJson = [parsedObject objectForKey:@"users"];
    for (NSDictionary *userJson in usersJson) {
        
        User *user = [self getUserBase:parsedObject userJson:userJson];
        [users addObject:user];
    }
    
    return users;
}

+ (UsersYesterday *)placeUsersYesterdayFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    UsersYesterday *usersYesterday =  [[UsersYesterday alloc] init];
    NSMutableArray *users = [[NSMutableArray alloc] init];
    NSString * msg = [parsedObject objectForKey:@"msg"];
    
    usersYesterday.hasAccess = YES;
    if ([msg isEqualToString:@"yes_not_checked_in"]) {
        usersYesterday.hasAccess = NO;
    }
    NSArray *usersJson = [parsedObject objectForKey:@"users"];
    for (NSDictionary *userJson in usersJson) {
        
        User *user = [self getUserBase:parsedObject userJson:userJson];
        [users addObject:user];
        
    }
    
    usersYesterday.users = users;
    
    return usersYesterday;
}

+ (Place *)getPlace:(NSDictionary *)clubJson
{
    Place *place = [[Place alloc] init];
    
    place.id  = [clubJson objectForKey:@"id"];
    place.title = [clubJson objectForKey:@"club_name"];
    place.phone = [clubJson objectForKey:@"club_phone"];
    place.address = [clubJson objectForKey:@"club_address"];
    place.email = [clubJson objectForKey:@"club_email"];
    place.site = [clubJson objectForKey:@"club_site"];
    place.avatar = [clubJson objectForKey:@"club_logo"];
    place.info = [clubJson objectForKey:@"club_info"];
    place.capacity = [[clubJson objectForKey:@"club_capacity"] intValue];
    place.ageRestriction = [clubJson objectForKey:@"club_age_restriction"];
    place.dressCode = [clubJson objectForKey:@"club_dress_code"];
    
    place.lon = [[clubJson objectForKey:@"club_loc"] objectForKey:@"lon"];
    place.lat = [[clubJson objectForKey:@"club_loc"] objectForKey:@"lat"];
    place.countOfUsers = [[clubJson objectForKey:@"active_checkins"] intValue];
    place.friendsCount = [[clubJson objectForKey:@"active_friends_checkins"] intValue];
    
    place.category = [clubJson objectForKey:@"category"];
    
    place.isFavorite = NO;
    NSNumber *isFavorite = [clubJson objectForKey:@"is_favorite"];
    if (isFavorite > 0) {
        place.isFavorite = YES;
    }
    
    //place.friendsCount = [[parsedObject objectForKey:@"friends_count"] intValue];
    
    NSMutableArray *photos = [[NSMutableArray alloc] init];
    
    NSArray *club_photos = [clubJson objectForKey:@"club_photos"];
    
    for (NSString *club_photo in club_photos) {
        [photos addObject:club_photo];
    }
    
    place.photos = photos;
    
    // get working hours
    NSMutableArray *clubWorkingHours = [[NSMutableArray alloc] init];
    NSArray *clubWorkingHoursJson = [clubJson objectForKey:@"club_working_hours"];
    for (NSDictionary *clubWorkingHourJson in clubWorkingHoursJson) {
        WorkingHour *workingHour = [[WorkingHour alloc] init];
        workingHour.day = [[clubWorkingHourJson objectForKey:@"day"] intValue];
        workingHour.startTime = [clubWorkingHourJson objectForKey:@"start_time"];
        workingHour.endTime = [clubWorkingHourJson objectForKey:@"end_time"];
        workingHour.status = [clubWorkingHourJson objectForKey:@"status"];
        [clubWorkingHours addObject:workingHour];
    }
    place.workingHours = clubWorkingHours;
    
    // get today working hours
    NSDictionary *clubWorkingHoursTodayJson = [clubJson objectForKey:@"club_today_working_hours"];
    WorkingHour *workingHour = [[WorkingHour alloc] init];
    
    if ([clubWorkingHoursTodayJson valueForKey:@"day"] != nil && [clubWorkingHoursTodayJson valueForKey:@"day"] != [NSNull null]) {
        workingHour.day = [[clubWorkingHoursTodayJson objectForKey:@"day"] intValue];
        workingHour.startTime = [clubWorkingHoursTodayJson objectForKey:@"start_time"];
        workingHour.endTime = [clubWorkingHoursTodayJson objectForKey:@"end_time"];
        workingHour.status = [clubWorkingHoursTodayJson objectForKey:@"status"];
        place.todayWorkingHours = workingHour;
    }
    return place;
}

+ (Place *)placeFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }

    NSMutableArray *users = [[NSMutableArray alloc] init];
    NSDictionary *clubJson = [parsedObject objectForKey:@"club"];
    NSArray *usersJson = [parsedObject objectForKey:@"users"];
      
    Place *place;
    place = [self getPlace:clubJson];
   
    for (NSDictionary *userJson in usersJson) {
        
        User *user = [self getUserBase:parsedObject userJson:userJson];
        [users addObject:user];
        /*[users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];*/

    }
    
    place.users = users;
    
    return place;
   
}

+ (BOOL)changePassFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSString *stats = [parsedObject objectForKey:@"status"];
    
    return [stats isEqualToString:@"ok"];
    
}

+ (User *)userFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSDictionary *userJson = [result objectForKey:@"user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
    
    return user;

}

+ (User *)tempUserFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    // extract specific value...
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    NSDictionary *userJson = [result objectForKey:@"_temp_user"];
    
    User *user = [self getUserBase:parsedObject userJson:userJson];
    
    return user;
    
}

+ (NSArray *) newsFromJSON:(NSData *)objectNotation error:(NSError *__autoreleasing *)error {
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *news = [[NSMutableArray alloc] init];
    
    NSArray *newsData = [parsedObject objectForKey:@"news"];
    
    for (NSDictionary *newsDic in newsData) {
        NewsData *newsObject = [[NewsData alloc] init];
        
        newsObject.type = [newsDic objectForKey:@"type"];
        
        newsObject.newsDescription = [newsDic objectForKey:@"description"];
        newsObject.photos = [newsDic objectForKey:@"photos"];
        
        NSMutableDictionary *venue = [newsDic objectForKey:@"venue"];
        newsObject.title = [venue objectForKey:@"club_name"];
        newsObject.avatarPath = [venue objectForKey:@"club_logo"];
        
        newsObject.buyLink = [newsDic objectForKey:@"buy_tickets"];
        newsObject.shareLink = [newsDic objectForKey:@"share"];
        
        NSString* dateStr = [newsDic objectForKey:@"created_on_formatted"];
        // Convert string to date object
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
        newsObject.createDate = [dateFormat dateFromString:dateStr];
        
        dateStr = [newsDic objectForKey:@"start_time"];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
        newsObject.startTime = [dateFormat dateFromString:dateStr];
        
        dateStr = [newsDic objectForKey:@"end_time"];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
        newsObject.endTime = [dateFormat dateFromString:dateStr];
        
        newsObject.tempDownlaodedPhotos = [[NSMutableDictionary alloc] init];
        
        [news addObject:newsObject];
    }
    
    return news;
}

+ (Chat *)chatFromJSON:(NSData *)objectNotation error:(NSError **)error;
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *conversations = [[NSMutableArray alloc] init];
    NSDictionary *resultJson = [parsedObject objectForKey:@"result"];
    NSArray *conversationsJson = [resultJson objectForKey:@"conversation"];
    
    Chat *chat = [[Chat alloc] init];
    
    chat.chatId  = [resultJson objectForKey:@"chat_id"];
    
    for (NSDictionary *conversationJson in conversationsJson) {
        Conversation *conf = [[Conversation alloc] init];
        
        conf.user_from = [conversationJson objectForKey:@"from_who"];
        conf.msg = [conversationJson objectForKey:@"msg"];
        conf.type = [conversationJson objectForKey:@"type"];
        conf.url = [conversationJson objectForKey:@"url"];
        conf.location = [conversationJson objectForKey:@"location"];
        
        NSString *dateStr = [conversationJson objectForKey:@"time"];
        
        // Convert string to date object
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
        conf.time = [dateFormat dateFromString:dateStr];
        
        [conversations addObject:conf];
    }
    
    NSDictionary *currentUserJson = [resultJson objectForKey:@"current_user"];
    User *currentUser = [self getUserBase:parsedObject userJson:currentUserJson];
    chat.currentUser = currentUser;
    
    NSDictionary *receiverJson = [resultJson objectForKey:@"receiver"];
    User *receiver = [self getUserBase:parsedObject userJson:receiverJson];
    chat.receiver = receiver;
    
    chat.conversations = conversations;
    
    return chat;
}

+ (ClubbookNotifications *)unreadMessagesFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    ClubbookNotifications *notifications = [[ClubbookNotifications alloc] init];
    
    notifications.countOfUnreadChats = (int)[[parsedObject objectForKey:@"unread_chat_count"] longValue];
    notifications.countOfPendingFriends = (int)[[parsedObject objectForKey:@"pending_friends_count"] longValue];
    notifications.fastCheckinPlaces = (int)[[parsedObject objectForKey:@"venue_count"] longValue];
    
    return notifications;
}

+ (NSArray *)conversationsJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSMutableArray *chats = [[NSMutableArray alloc] init];
    
    NSDictionary *resultJson = [parsedObject objectForKey:@"result"];
    
    NSArray *chatsJson = [resultJson objectForKey:@"chats"];
    
    for (NSDictionary *chatJson in chatsJson) {
        Chat *chat = [[Chat alloc] init];
        chat.chatId = [chatJson objectForKey:@"chat_id"];
        
        NSDictionary *currentUserJson = [chatJson objectForKey:@"current_user"];
        User *currentUser = [self getUserBase:parsedObject userJson:currentUserJson];
        chat.currentUser = currentUser;
        
        NSDictionary *receiverJson = [chatJson objectForKey:@"receiver"];
        User *receiver = [self getUserBase:parsedObject userJson:receiverJson];
        chat.receiver = receiver;
        
        chat.unreadMessages = [[chatJson objectForKey:@"unread_messages"] intValue];
        
        NSArray *conversationsJson = [chatJson objectForKey:@"conversation"];
        
        NSMutableArray *conversations = [[NSMutableArray alloc] init];
        
        for (NSDictionary *conversationJson in conversationsJson) {
            Conversation *conf = [[Conversation alloc] init];
            
            conf.user_from = [conversationJson objectForKey:@"from_who"];
            conf.msg = [conversationJson objectForKey:@"msg"];
            conf.type = [conversationJson objectForKey:@"type"];
            conf.url = [conversationJson objectForKey:@"url"];
            conf.location = [conversationJson objectForKey:@"location"];
            
            NSNumber * isRead = (NSNumber *)[conversationJson objectForKey: @"read"];
            conf.read = (isRead && [isRead boolValue] == YES);
            
            NSString *dateStr = [conversationJson objectForKey:@"time"];
            
            // Convert string to date object
            NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
            [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
            [dateFormat setDateFormat:@"yyyy-MM-dd, HH:mm:ss"];
            conf.time = [dateFormat dateFromString:dateStr];
            
            [conversations addObject:conf];
        }
        
        chat.conversations = conversations;
        
        [chats addObject:chat];
    }
    
    return chats;
}

+ (Config *)getConfigFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    NSDictionary *result = [parsedObject objectForKey:@"result"];
    
    Config *config = [[Config alloc] init];
    config.maxCheckinRadius = [[result objectForKey:@"chekin_max_distance"] intValue];
    config.maxFailedCheckin = [[result objectForKey:@"max_failed_checkin_count"] intValue];
    config.checkinUpdateTime = [[result objectForKey:@"update_checkin_status_interval"] intValue];
    
    return config;
}

+ (User *)getUserBase:(NSDictionary *)parsedObject userJson:(NSDictionary *)userJson
{
    User *user = [[User alloc] init];
    
    NSString * error = [parsedObject valueForKey:@"err"];
    
    if(error != nil)
    {
        user.error = error;
    }
    
    user.id  = [userJson objectForKey:@"id"];
    if (!user.id) {
        user.id  = [userJson objectForKey:@"_id"];
    }
    user.email  = [userJson objectForKey:@"email"];
    user.name  = [userJson objectForKey:@"name"];
    user.gender  = [userJson objectForKey:@"gender"];
    user.avatar  = [userJson objectForKey:@"avatar"];
    user.country = [userJson objectForKey:@"country"];
    user.access_token = [userJson objectForKey:@"access_token"];
    user.friend_status = [userJson objectForKey:@"friend_status"];
    user.bio = [userJson objectForKey:@"bio"];
    
    NSNumber * isFriend = (NSNumber *)[userJson objectForKey: @"is_friend"];
    user.isFriend = (isFriend && [isFriend boolValue] == YES);
    
    NSNumber * isPush = (NSNumber *)[userJson objectForKey: @"push"];
    user.push = (isPush && [isPush boolValue] == YES);
    
    NSNumber * isVisibleNearby = (NSNumber *)[userJson objectForKey: @"is_visible_nearby"];
    user.isVisibleNearby = (isVisibleNearby && [isVisibleNearby boolValue] == YES);
    
    NSNumber * isBlocked = (NSNumber *)[userJson objectForKey: @"is_blocked"];
    user.isBlocked = (isBlocked && [isBlocked boolValue] == YES);
    
    user.isFb = NO;
     if ([userJson valueForKey:@"fb_id"] != nil && [userJson valueForKey:@"fb_id"] != [NSNull null]) {
         user.isFb = YES;
     }
    
    if ([userJson valueForKey:@"checkin"] != nil && [userJson valueForKey:@"checkin"] != [NSNull null]) {
        NSArray * checkins = [userJson objectForKey:@"checkin"];
        if (checkins.count > 0) {
           NSDictionary *checkinJson = [checkins objectAtIndex: 0];
            
           NSNumber * isActive = (NSNumber *)[checkinJson objectForKey: @"active"];
            if(isActive && [isActive boolValue] == YES)
            {
                if ([[checkinJson objectForKey:@"club"] isKindOfClass:[NSDictionary class]]) {
                    NSDictionary *clubJson = [checkinJson objectForKey:@"club"];
                    
                    user.place = [self getPlace:clubJson];
                    user.currentCheckinClubName = [clubJson objectForKey:@"club_name"];
                }
            }

        }
    }
    
    if ([userJson valueForKey:@"age"] != nil && [userJson valueForKey:@"age"] != [NSNull null]) {
        user.age = [userJson objectForKey:@"age"];
    }
    
    if ([userJson valueForKey:@"dob_format"] != nil && [userJson valueForKey:@"dob_format"] != [NSNull null]) {
        NSString *dateStr = [userJson objectForKey:@"dob_format"];
        
        // Convert string to date object
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setTimeZone:[NSTimeZone timeZoneWithName:@"UTC"]];
        [dateFormat setDateFormat:@"yyyy-MM-dd"];
        user.dob = [dateFormat dateFromString:dateStr];

    }
    
    NSMutableArray *photos = [[NSMutableArray alloc] init];
    
    NSArray *user_photos = [userJson objectForKey:@"photos"];
    
    for (NSString *user_photo in user_photos) {
        [photos addObject:user_photo];
    }
    
    user.photos = photos;
    return user;
}

@end
