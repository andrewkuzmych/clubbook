//
//  ObjectBuilder.h
//  BrowseMeetup
//
//  Created by Andrew on 10/1/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "User.h"
#import "Place.h"
#import "Chat.h"
#import "ClubbookNotifications.h"
#import "Config.h"
#import "FriendsResult.h"
#import "UsersYesterday.h"


@interface ObjectBuilder : NSObject

+ (User *)fbLoginFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)signupFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)signinFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)placesFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)eventsFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (Place *)placeFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)placeUsersFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (UsersYesterday *)placeUsersYesterdayFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)checkinFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)userFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)newsFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)tempUserFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (Chat *)chatFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)conversationsJSON:(NSData *)objectNotation error:(NSError **)error;
+ (FriendsResult *)friendsJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)usersJSON:(NSData *)objectNotation error:(NSError **)error;
+ (BOOL)changePassFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (ClubbookNotifications *)unreadMessagesFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (Config *)getConfigFromJSON:(NSData *)objectNotation error:(NSError **)error;

@end
