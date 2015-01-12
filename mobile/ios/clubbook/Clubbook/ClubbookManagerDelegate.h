//
//  BonusClubManagerDelegate.h
//  BrowseMeetup
//
//  Created by Andrew on 10/2/13.
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


@protocol ClubbookManagerDelegate <NSObject>

@optional

  - (void)didFbLoginUser:(User *)user;
  - (void)didSignapUser:(User *)user;
  - (void)didSigninUser:(User *)user;
  - (void)didChat:(NSString *)result;
  - (void)didReceivePlaces:(NSArray *)places andTypes:(NSArray*) types;
  - (void)didReceivePlace:(Place *)place;
  - (void)didReceivePlaceUsers:(NSArray *)users;
  - (void)didReceivePlaceUsersYesterday:(UsersYesterday *)usersYesterday;
  - (void)didReceiveUsersCheckedin:(NSArray *)users;
  - (void)didReceiveUser:(User *)user;
  - (void)didReceiveFriend:(User *)user;
  - (void)didRetrieveFriends:(FriendsResult *)friendsResult;
  - (void)didRetrievePendingFriends:(FriendsResult *)friendsResult;
  - (void)didReceiveConversation:(Chat *)chat;
  - (void)didReceiveConversations:(NSArray *)chats;
  - (void)didDeleteConversation:(NSString *)result;
  - (void)didReceivedNotifications:(ClubbookNotifications *)notifications;
  - (void)didReadChat:(NSString *)result;
  - (void)didCheckin:(User *)user userInfo:(NSObject*) userInfo;
  - (void)didCheckout:(User *)user userInfo:(NSObject *) userInfo;
  - (void)didUpdateCheckin:(User *)user;
  - (void)didAddUserImage:(User *)user;
  - (void)didUpdateUserImage:(User *)user;
  - (void)didDeleteUserImage:(User *)user;
  - (void)didUpdateUser:(User *)user;
  - (void)didDeleteUser:(NSString *)result;
  - (void)didUpdateUserLocation:(NSString *)result;
  - (void)didChangePass:(BOOL)result;
  - (void)didSendFriend:(User *)user;
  - (void)didConfirmFriend:(User *)user;
  - (void)didRemoveFriend:(User *)user;
  - (void)didRemoveFriendRequest:(User *)user;
  - (void)didChangeVisibleNearby:(User *)user;
  - (void)didChangePush:(User *)user;
  - (void)didGetConfig:(Config *)config;

  - (void)didBlockUser:(NSString *)result;
  - (void)didUnblockUser:(NSString *)result;
  - (void)didInviteFbFriends:(NSString *)result;
  - (void)didFindFbFriends:(NSArray *)users;

  - (void)failedWithError:(NSError *)error;

@end
