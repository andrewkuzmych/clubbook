//
//  FloatingButton.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FloatingButton : UIButton

@property BOOL statusOn;

- (void) changeStatus:(BOOL) isOn;

@end
