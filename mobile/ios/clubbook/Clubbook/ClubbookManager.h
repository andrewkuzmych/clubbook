//
//  BonusClubManager.h
//  BrowseMeetup
//
//  Created by Andrew on 10/2/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

#import "ClubbookManagerDelegate.h"
#import "ClubbookCommunicatorDelegate.h"

@class ClubbookCommunicator;


@interface ClubbookManager : NSObject<ClubbookCommunicatorDelegate>

@property (strong, nonatomic) ClubbookCommunicator *communicator;
@property (weak, nonatomic) id<ClubbookManagerDelegate> delegate;

- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob;

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;

- (void)signinUser:(NSString *) email pass:(NSString *) pass;

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type;

- (void)retrievePlaces:(double) distance lat:(double) lat lon:(double) lon;

- (void)retrievePlace:(NSString *) clubId userId:(NSString *) userId;

- (void)retrieveUser:(NSString *) userId;

- (void)retrieveFriend:(NSString *) friendId currnetUserId:(NSString *) currnetUserId;

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser;

- (void)retrieveConversations:(NSString *) userId;

- (void)retrieveFriends:(NSString *) userId;

- (void)retrievePendingFriends:(NSString *) userId;

- (void)unreadMessages:(NSString *) userId;

- (void)readChat:(NSString *) fromUser toUser:(NSString *) toUser;

- (void)checkin:(NSString *) clubId userId:(NSString *) userId userInfo:(NSObject *) userInfo;

- (void)checkout:(NSString *) clubId userId:(NSString *) userId userInfo:(NSObject *) userInfo;

- (void)updateCheckin:(NSString *) clubId userId:(NSString *) userId;

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar;

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId;

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId;

- (void)changeUserPush:(NSString *) userId push:(BOOL) push;

- (void)updateUser:(NSString *) userId name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio;

- (void)sendFriendReguest:(NSString *) userId friendId:(NSString *) friendId;

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId;

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId;

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId;

- (void) getConfig;

@end
