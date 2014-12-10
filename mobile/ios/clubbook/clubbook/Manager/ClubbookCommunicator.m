//
//  ClubbookCommunicator.m
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubbookCommunicator.h"
#import "ClubbookCommunicatorDelegate.h"

#define kBgQueue dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0) //1

@implementation ClubbookCommunicator


- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name  email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob;

{
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *urlAsString = [NSString stringWithFormat:@"%@signin/fb", baseURL];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         fbId, @"fb_id",
         fbAccessToken, @"fb_access_token",
         @"", @"fb_access_expires",
         gender, @"gender",
         name, @"name",
         email, @"email",
         avatar, @"avatar",
         bio, @"bio",
         country, @"country",
         dob, @"dob",
         //dob, @"dob",
         //fbCity, @"fb_city",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate fbLoginJSON:data];
                });
            }
        }];
    });
}

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/image?access_token=%@", baseURL, userId, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         avatar, @"avatar",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate addUserImageJSON:data];
                });
            }
        }];
    });
}

- (void)changePass:(NSString *) oldPass newPass:(NSString *) newPass accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/update_pass?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         oldPass, @"old_password",
         newPass, @"new_password",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"PUT"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate changePassJSON:data];
                });
            }
        }];
    });

}

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/image/%@?access_token=%@", baseURL, userId, objectId, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         @"1", @"is_avatar",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"PUT"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate updateUserImageJSON:data];
                });
            }
        }];
    });
}

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/image/%@?access_token=%@", baseURL, userId, objectId, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"DELETE"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate deleteUserImageJSON:data];
                });
            }
        }];
    });

}

- (void)changeUserVisibilityNearby:(NSString *) accessToken isVisible:(BOOL) visible
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         (visible) ? @"true" : @"false", @"is_visible_nearby",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"PUT"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate changeUserVisibleNearbyJSON:data];
                });
            }
        }];
    });

}

- (void)changeUserPush:(NSString *) accessToken push:(BOOL) push
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         (push) ? @"true" : @"false", @"push_not",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"PUT"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate changeUserPushJSON:data];
                });
            }
        }];
    });

}

- (void)updateUser:(NSString *) accessToken name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         name, @"name",
         gender, @"gender",
         dob, @"dob",
         country, @"country",
         bio, @"bio",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"PUT"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate updateUserJSON:data];
                });
            }
        }];
    });

}

- (void)deleteUser:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"DELETE"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate deleteUserJSON:data];
                });
            }
        }];
    });

}

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@signup", baseURL];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         name, @"name",
         gender, @"gender",
         email, @"email",
         city, @"city",
         pass, @"password",
         dob, @"dob",
         avatar, @"avatar",
         country, @"country",
         bio, @"bio",
         //dob, @"dob",
         //fbCity, @"fb_city",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate signupJSON:data];
                });
            }
        }];
    });
}

- (void)signinUser:(NSString *) email pass:(NSString *) pass;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@signinmail", baseURL];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         email, @"email",
         pass, @"password",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate signinJSON:data];
                });
            }
        }];
    });
}

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/chat?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         msg_type, @"msg_type",
         msg, @"msg",
         user_from, @"user_from",
         user_to, @"user_to",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate chatJSON:data];
                });
            }
        }];
    });
}

- (void)updateUserLocation:(double) lat lon:(double) lon accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/location/update?user_lat=%f&user_lon=%f&access_token=%@", baseURL, lat, lon, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate updateUserLocationJSON:data];
                }
            });
        }];
    });
}

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/chat/%@/%@?access_token=%@", baseURL, fromUser, toUser,accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedConversationJSON:data];
                }
            });
        }];
    });
}

- (void) readChat:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/chat/%@/%@/read?access_token=%@", baseURL, fromUser, toUser, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate readChatJSON:data];
                }
            });
        }];
    });
}

- (void) unreadMessages:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me/notifications?access_token=%@", baseURL, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate unreadMessagesJSON:data];
                }
            });
        }];
    });
}

- (void)retrieveConversations:(NSString *) userId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/chat/%@?access_token=%@", baseURL, userId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedConversationsJSON:data];
                }
            });
        }];
    });
    
}


- (void)deleteConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/chat/%@/%@/delete?access_token=%@", baseURL, fromUser, toUser,accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate deleteConversationJSON:data];
                }
            });
        }];
    });
}


- (void)retrievePlaces:(double) lat lon:(double) lon take:(int) take skip:(int) skip distance:(int) distance type:(NSString*) type search:(NSString*) search accessToken:(NSString *) accessToken;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        int distanceTemp = distance;
        if (distanceTemp == 0) {
            distanceTemp = 100000;
        }
        //make base url
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club?user_lat=%f&user_lon=%f&type=%@&search=%@&skip=%d&take=%d&distance=%d&access_token=%@", baseURL, lat, lon, type, search, skip, take, distanceTemp, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedPlacesJSON:data];
                }
            });
            
        }];
        
    });
}

- (void)retrievePlace:(NSString *) clubId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedPlaceJSON:data];
                }
            });
            
        }];
        
    });
}

- (void)retrievePlaceUsers:(NSString *) clubId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@/users?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedPlaceUsersJSON:data];
                }
            });
        }];
    });
}

- (void)retrievePlaceUsersYesterday:(NSString *) clubId accessToken:(NSString *) accessToken
{

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@/users/yesterday?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedPlaceUsersYesterdayJSON:data];
                }
            });
        }];
    });
}

- (void)receivedUsers:(bool)all take:(int) take skip:(int) skip lat:(double) lat lon:(double) lon distance:(double) distance accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/users/checkedin?user_lat=%f&user_lon=%f&skip=%d&take=%d&distance=%f&access_token=%@", baseURL, lat, lon, skip, take, distance, accessToken];
        if (all) {
             urlAsString = [NSString stringWithFormat:@"%@obj/users/around?user_lat=%f&user_lon=%f&skip=%d&take=%d&distance=%f&access_token=%@", baseURL, lat, lon, skip, take, distance, accessToken];
        }
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedUsersCheckedinJSON:data];
                }
            });
        }];
    });
}

- (void)retrieveUser:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/me?access_token=%@", baseURL, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedUserJSON:data];
                }
            });
        }];
    });
}

- (void)retrieveFriend:(NSString *) friendId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@?access_token=%@", baseURL, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedFriendJSON:data];
                }
            });
        }];
    });
}


- (void)checkin:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@/checkin?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate checkinJSON:data userInfo:userInfo];
                }
            });
        }];
    });
}

- (void)checkout:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@/checkout?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                //if (error) {
                //    [self.delegate failedWithError:error];
                //} else {
                    [self.delegate checkoutJSON:data userInfo:userInfo];
                //}
            });
        }];
    });

}

- (void)updateCheckin:(NSString *) clubId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/club/%@/update?access_token=%@", baseURL, clubId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate updateCheckinJSON:data];
                }
            });
        }];
    });
}


- (void)sendFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends/%@/friend?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate sendFriendJSON:data];
                }
            });
        }];
    });
}

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends/%@/confirm?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate confirmFriendJSON:data];
                }
            });
        }];
    });
}

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends/%@/unfriend?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate removeFriendJSON:data];
                }
            });
        }];
    });
}

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends/%@/remove?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate removeFriendRequestJSON:data];
                }
            });
        }];
    });
}

- (void)retrieveFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends?access_token=%@", baseURL, userId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedFriendsJSON:data];
                }
            });
        }];
    });
}

- (void)retrievePendingFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/friends/pending?access_token=%@", baseURL, userId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate receivedPendingFriendsJSON:data];
                }
            });
        }];
    });
}



- (void)getConfig
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/config", baseURL];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate getConfigJSON:data];
                }
            });
        }];
    });
}


- (void)blockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/block/%@?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate blockUserJSON:data];
                }
            });
        }];
    });
}


- (void)unblockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        // switch to a background thread and perform your expensive operation
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/unblock/%@?access_token=%@", baseURL, userId, friendId, accessToken];
        
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlAsString]];
        
        [NSURLConnection sendAsynchronousRequest:urlRequest queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    [self.delegate failedWithError:error];
                } else {
                    [self.delegate unblockUserJSON:data];
                }
            });
        }];
    });

}

- (void)inviteFbFriends:(NSString *) userId fb_ids:(NSArray *) fb_ids accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *fbIdCommaSeparated = [fb_ids componentsJoinedByString:@","];
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/%@/fb/invite?access_token=%@", baseURL, userId, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         fbIdCommaSeparated, @"fb_ids",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate inviteFbFriendsJSON:data];
                });
            }
        }];
    });

}

- (void)findFbFriends:(NSArray *) fb_ids accessToken:(NSString *) accessToken
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSString *fbIdCommaSeparated = [fb_ids componentsJoinedByString:@","];
        NSString *urlAsString = [NSString stringWithFormat:@"%@obj/user/fb/find?access_token=%@", baseURL, accessToken];
        NSDictionary * data  =
        [NSDictionary
         dictionaryWithObjectsAndKeys:
         fbIdCommaSeparated, @"fb_ids",
         nil];
        
        NSMutableURLRequest *request;
        request = [self generateRequest:data url:urlAsString method:@"POST"];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            
            if (error) {
                [self.delegate failedWithError:error];
            } else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.delegate findFbFriendsJSON:data];
                });
            }
        }];
    });
}

- (NSMutableURLRequest *)generateRequest:(NSDictionary *)data url:(NSString *)url method:(NSString *)method
{

    NSMutableArray * content = [NSMutableArray array];
    
    for(NSString * key in data)
        [content
         addObject: [NSString stringWithFormat: @"%@=%@", key, data[key]]];
    
    NSString * body = [content componentsJoinedByString: @"&"];
    NSData * bodyData = [body dataUsingEncoding: NSUTF8StringEncoding];
    
    NSMutableURLRequest * request =
    [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString:url]];
    
    [request setTimeoutInterval:30.0f];
    
    NSString * msgLength =  [NSString stringWithFormat: @"%d", [bodyData length]];
    
    [request
     addValue: @"application/x-www-form-urlencoded; charset=utf-8"
     forHTTPHeaderField: @"Content-Type"];
    
    [request addValue: msgLength forHTTPHeaderField: @"Content-Length"];
    [request setHTTPMethod: method];
    [request setHTTPBody: bodyData];
    return request;
}


@end
