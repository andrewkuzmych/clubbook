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
#import "UnreadMessages.h"
#import "Config.h"

@protocol ClubbookManagerDelegate <NSObject>

@optional

  - (void)didFbLoginUser:(User *)user;
  - (void)didSignapUser:(User *)user;
  - (void)didSigninUser:(User *)user;
  - (void)didChat:(NSString *)result;
  - (void)didReceivePlaces:(NSArray *)places;
  - (void)didReceivePlace:(Place *)place;
  - (void)didReceiveUser:(User *)user;
  - (void)didReceiveFriend:(User *)user;
  - (void)didRetrieveFriends:(NSArray *)friends;
  - (void)didRetrievePendingFriends:(NSArray *)friends;
  - (void)didReceiveConversation:(Chat *)chat;
  - (void)didReceiveConversations:(NSArray *)chats;
  - (void)didUnreadMessages:(UnreadMessages *)unreadMessages;
  - (void)didReadChat:(NSString *)result;
  - (void)didCheckin:(User *)user userInfo:(NSObject*) userInfo;
  - (void)didCheckout:(User *)user userInfo:(NSObject *) userInfo;
  - (void)didUpdateCheckin:(User *)user;
  - (void)didAddUserImage:(User *)user;
  - (void)didUpdateUserImage:(User *)user;
  - (void)didDeleteUserImage:(User *)user;
  - (void)didUpdateUser:(User *)user;
  - (void)didDeleteUser:(NSString *)result;
  - (void)didSendFriend:(User *)user;
  - (void)didConfirmFriend:(User *)user;
  - (void)didRemoveFriend:(User *)user;
  - (void)didRemoveFriendRequest:(User *)user;
  - (void)didChangePush:(User *)user;
  - (void)didGetConfig:(Config *)config;

  - (void)failedWithError:(NSError *)error;

@end
