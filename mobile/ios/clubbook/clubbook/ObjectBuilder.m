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
    
    NSArray *venues = [parsedObject objectForKey:@"clubs"];
    
    NSLog(@"Count %d", venues.count);
    
    for (NSDictionary *placeDic in venues) {
        Place *place = [[Place alloc] init];
        
        place.id = [placeDic objectForKey:@"id"];
        place.title = [placeDic objectForKey:@"club_name"];
        place.address = [placeDic objectForKey:@"club_address"];
        place.phone = [placeDic objectForKey:@"club_phone"];
        place.avatar = [placeDic objectForKey:@"club_logo"];
        place.lat = [[placeDic objectForKey:@"club_loc"] objectForKey:@"lat"];
        place.lon = [[placeDic objectForKey:@"club_loc"] objectForKey:@"lon"];
        place.countOfUsers = [[placeDic objectForKey:@"active_checkins"] intValue];
        
        [places addObject:place];
    }
    
    return places;
}

+ (NSArray *)friendsJSON:(NSData *)objectNotation error:(NSError **)error
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
    
    return friends;
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

    place.friendsCount = [[parsedObject objectForKey:@"friends_count"] intValue];

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
   
    for (NSDictionary *userJson in usersJson) {
        
        User *user = [self getUserBase:parsedObject userJson:userJson];
               [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];
        [users addObject:user];

    }
    
    place.users = users;
    
    return place;
   
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
    //NSDictionary *clubJson = [parsedObject objectForKey:@"club"];
    NSArray *conversationsJson = [resultJson objectForKey:@"conversation"];
    
    Chat *chat = [[Chat alloc] init];
    
    chat.chatId  = [resultJson objectForKey:@"chat_id"];
    
    for (NSDictionary *conversationJson in conversationsJson) {
        Conversation *conf = [[Conversation alloc] init];
        
        conf.user_from = [conversationJson objectForKey:@"from_who"];
        conf.msg = [conversationJson objectForKey:@"msg"];
        conf.type = [conversationJson objectForKey:@"type"];
        
        NSString *dateStr = [conversationJson objectForKey:@"time"];// @"Tue, 25 May 2010 12:53:58 +0000";
        
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

+ (UnreadMessages *)unreadMessagesFromJSON:(NSData *)objectNotation error:(NSError **)error
{
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil) {
        *error = localError;
        return nil;
    }
    
    UnreadMessages *unreadMessages = [[UnreadMessages alloc] init];
    
    unreadMessages.countOfUnreadChats = [[parsedObject objectForKey:@"unread_chat_count"] longValue];
    unreadMessages.countOfPendingFriends = [[parsedObject objectForKey:@"pending_friends_count"] longValue];
    
    return unreadMessages;
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

+ (User *)getUserBase:(NSDictionary *)parsedObject userJson:(NSDictionary *)userJson
{
    User *user = [[User alloc] init];
    
    NSString * error = [parsedObject valueForKey:@"err"];
    
    if(error != nil)
    {
        user.error = error;
    }
    
    user.id  = [userJson objectForKey:@"id"];
    user.email  = [userJson objectForKey:@"email"];
    user.name  = [userJson objectForKey:@"name"];
    user.gender  = [userJson objectForKey:@"gender"];
    user.avatar  = [userJson objectForKey:@"avatar"];
    user.country = [userJson objectForKey:@"country"];
    user.friend_status = [userJson objectForKey:@"friend_status"];
    user.bio = [userJson objectForKey:@"bio"];
    
    NSNumber * isFriend = (NSNumber *)[userJson objectForKey: @"is_friend"];
    user.isFriend = (isFriend && [isFriend boolValue] == YES);
    
    if ([userJson valueForKey:@"checkin"] != nil && [userJson valueForKey:@"checkin"] != [NSNull null]) {
        NSArray * checkins = [userJson objectForKey:@"checkin"];
        if (checkins.count > 0) {
           NSDictionary *checkinJson = [checkins objectAtIndex: 0];
            
           NSNumber * isActive = (NSNumber *)[checkinJson objectForKey: @"active"];
            if(isActive && [isActive boolValue] == YES)
            {
                NSDictionary *clubJson = [checkinJson objectForKey:@"club"];
               user.currentCheckinClubName = [clubJson objectForKey:@"club_name"];
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
